package tv.twitch.moonmoon.rpengine2.data.player.impl;

import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.task.Callback;
import tv.twitch.moonmoon.rpengine2.util.Lang;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractRpPlayerRepo implements RpPlayerRepo {

    protected final RpPlayerDbo playerDbo;
    protected final AttributeRepo attributeRepo;
    protected final SelectRepo selectRepo;

    protected Map<UUID, RpPlayer> players;
    protected Map<String, RpPlayer> playerNameMap;

    public AbstractRpPlayerRepo(
        RpPlayerDbo playerDbo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo
    ) {
        this.playerDbo = Objects.requireNonNull(playerDbo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
    }

    protected abstract Optional<String> getPlayerName(UUID playerId);

    protected Optional<Option> getMarkerOption(RpPlayer player) {
        RpPlayerAttribute marker = attributeRepo.getMarker()
            .flatMap(m -> player.getAttribute(m.getId()))
            .orElse(null);

        if (marker == null) {
            return Optional.empty();
        }

        String selectName = marker.getName();
        //noinspection OptionalGetWithoutIsPresent
        int optionId = (Integer) marker.getValue().get();

        return selectRepo.getSelect(selectName)
            .flatMap(select -> select.getOption(optionId));
    }

    private Result<RpPlayer> createPlayer(UUID playerId) {
        String playerName = getPlayerName(playerId).orElse(null);
        if (playerName == null) {
            return Result.error("player not found");
        }

        Result<Long> newPlayerId = playerDbo.insertPlayer(playerName, playerId);

        Optional<String> err = newPlayerId.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        if (newPlayerId.get() == 0) {
            // player already existed
            RpPlayer existing = playerNameMap.get(playerName);
            if (existing != null && !playerId.equals(existing.getUUID())) {
                onWarning(Lang.getString("playerIdConflict"));
                return Result.ok(existing);
            }

            return Result.ok(players.get(playerId));
        }

        Result<RpPlayer> p = reloadPlayer(playerId);

        err = p.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        RpPlayer newPlayer = p.get();

        for (Attribute attribute : attributeRepo.getAttributes()) {
            Object defaultValue = attribute.getDefaultValue().orElse(null);
            setAttributeAsync(newPlayer, attribute.getId(), defaultValue, r ->
                r.getError().ifPresent(this::onWarning)
            );
        }

        return Result.ok(newPlayer);
    }

    @Override
    public Set<RpPlayer> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

    @Override
    public Result<RpPlayer> getPlayer(UUID player) {
        Objects.requireNonNull(player);
        return Optional.ofNullable(players.get(player))
            .map(Result::ok)
            .orElseGet(() -> handleResult(() -> createPlayer(player)));
    }

    @Override
    public Optional<RpPlayer> getLoadedPlayer(String name) {
        return Optional.ofNullable(playerNameMap.get(name));
    }

    @Override
    public String getIdentityPlain(RpPlayer player) {
        return attributeRepo.getIdentity()
            .flatMap(i -> player.getAttribute(i.getId()))
            .flatMap(i -> i.getValue().map(Object::toString))
            .orElseGet(player::getUsername);
    }

    @Override
    public String getTitle(RpPlayer player) {
        RpPlayerAttribute title = attributeRepo.getTitle()
            .flatMap(t -> player.getAttribute(t.getId()))
            .orElse(null);

        if (title == null) {
            return "";
        }

        if (title.getType() == AttributeType.Select) {
            String selectName = title.getName();
            //noinspection OptionalGetWithoutIsPresent
            int optionId = (Integer) title.getValue().get();

            return selectRepo.getSelect(selectName)
                .flatMap(select -> select.getOption(optionId))
                .map(Option::getName)
                .orElse("");
        }

        return title.getValue()
            .map(Object::toString)
            .orElse("");
    }

    @Override
    public void setAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Callback<Void> callback
    ) {
        UUID playerId = player.getUUID();
        Optional<RpPlayerAttribute> existing = player.getAttribute(attributeId);

        if (existing.isPresent()) {
            playerDbo.updatePlayerAttributeAsync(player.getId(), attributeId, value, r ->
                callback.accept(handleAndReloadPlayer(playerId, r))
            );
        } else {
            playerDbo.insertPlayerAttributeAsync(player.getId(), attributeId, value, r ->
                callback.accept(handleAndReloadPlayer(playerId, r))
            );
        }
    }

    @Override
    public void removeAttributesAsync(int attributeId, Callback<Void> callback) {
        playerDbo.deletePlayerAttributesAsync(attributeId, r ->
            callback.accept(handleAndReloadPlayers(r))
        );
    }

    @Override
    public void startSessionAsync(RpPlayer player) {
        playerDbo.setSessionAsync(player.getId(), Instant.now(), r ->
            handleAndReloadPlayer(player.getUUID(), r).getError().ifPresent(this::onWarning)
        );
    }

    @Override
    public void clearSession(RpPlayer player) {
        handleAndReloadPlayer(
            player.getUUID(),
            playerDbo.setSession(player.getId(), null)
        ).getError().ifPresent(this::onWarning);
    }

    @Override
    public void setPlayed(RpPlayer player, Duration duration) {
        handleAndReloadPlayer(
            player.getUUID(),
            playerDbo.setPlayed(player.getId(), duration)
        ).getError().ifPresent(this::onWarning);
    }

    @Override
    public Result<Void> load() {
        Optional<String> err = playerDbo.clearSessions().getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        Result<Set<RpPlayer>> r = playerDbo.selectPlayers();

        err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            onLoad(r.get());
            return Result.ok(null);
        }
    }

    @Override
    public Result<Void> reloadPlayers() {
        Result<Set<RpPlayer>> reloadedPlayers = handleResult(playerDbo::selectPlayers);

        Optional<String> err = reloadedPlayers.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            onLoad(reloadedPlayers.get());
            return Result.ok(null);
        }
    }

    @Override
    public void shutdown() {
        // update play time
        Instant now = Instant.now();
        for (RpPlayer player : players.values()) {
            Instant sessionStart = player.getSessionStart().orElse(null);
            if (sessionStart != null) {
                Duration sessionDuration = Duration.between(sessionStart, now);
                setPlayed(player, player.getPlayed().plus(sessionDuration));
                clearSession(player);
            }
        }
    }

    private void onLoad(Set<RpPlayer> loadedPlayers) {
        players = Collections.synchronizedMap(loadedPlayers.stream()
            .collect(Collectors.toMap(RpPlayer::getUUID, Function.identity())));
        playerNameMap = Collections.synchronizedMap(loadedPlayers.stream()
            .collect(Collectors.toMap(RpPlayer::getUsername, Function.identity())));
    }

    protected Result<RpPlayer> reloadPlayer(UUID playerId) {
        Result<RpPlayer> updatedPlayer = playerDbo.selectPlayer(playerId);

        Optional<String> err = updatedPlayer.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        RpPlayer p = updatedPlayer.get();
        updatePlayer(p);

        return Result.ok(p);
    }

    private <T> Result<Void> handleAndReloadPlayer(UUID playerId, Result<T> r) {
        Optional<String> err = handleResult(() -> r).getError();
        return err
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> handleResult(() -> reloadPlayer(playerId)).getError()
                .<Result<Void>>map(Result::error)
                .orElseGet(() -> Result.ok(null))
            );
    }

    private void updatePlayer(RpPlayer player) {
        players.put(player.getUUID(), player);
        playerNameMap.put(player.getUsername(), player);
    }

    private <T> Result<Void> handleAndReloadPlayers(Result<T> r) {
        Optional<String> err = handleResult(() -> r).getError();
        return err
            .<Result<Void>>map(Result::error)
            .orElseGet(this::reloadPlayers);
    }
}
