package tv.twitch.moonmoon.rpengine2.data.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class RpPlayerRepoImpl implements RpPlayerRepo {

    private final RpPlayerDbo playerDbo;
    private final AttributeRepo attributeRepo;
    private final SelectRepo selectRepo;
    private final Logger log;

    private Map<UUID, RpPlayer> players;
    private Map<String, RpPlayer> playerNameMap;

    @Inject
    public RpPlayerRepoImpl(
        RpPlayerDbo playerDbo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo, @PluginLogger Logger log
    ) {
        this.playerDbo = Objects.requireNonNull(playerDbo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public Set<RpPlayer> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

    @Override
    public Result<RpPlayer> getPlayer(OfflinePlayer player) {
        Objects.requireNonNull(player);
        return Optional.ofNullable(players.get(player.getUniqueId()))
            .map(Result::ok)
            .orElseGet(() -> handleResult(() -> createPlayer(player)));
    }

    @Override
    public Optional<RpPlayer> getLoadedPlayer(String name) {
        return Optional.ofNullable(playerNameMap.get(name));
    }

    @Override
    public String getIdentity(RpPlayer player) {
        return getPrefix(player) + getIdentityPlain(player);
    }

    @Override
    public String getIdentityPlain(RpPlayer player) {
        return attributeRepo.getIdentity()
            .flatMap(i -> player.getAttribute(i.getId()))
            .flatMap(i -> i.getValue().map(Object::toString))
            .orElseGet(player::getUsername);
    }

    @Override
    public String getPrefix(RpPlayer player) {
        return getMarkerColor(player)
            .map(c -> net.md_5.bungee.api.ChatColor.valueOf(c.name()))
            .map(net.md_5.bungee.api.ChatColor::toString)
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
    public Result<Void> load() {
        Result<Set<RpPlayer>> r = playerDbo.selectPlayers();

        Optional<String> err = r.getError();
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

    private Result<RpPlayer> createPlayer(OfflinePlayer player) {
        Result<Long> newPlayerId = playerDbo.insertPlayer(player);
        Optional<String> err = newPlayerId.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        UUID playerId = player.getUniqueId();

        if (newPlayerId.get() == 0) {
            // player already existed
            RpPlayer existing = playerNameMap.get(player.getName());
            if (existing != null && !playerId.equals(existing.getUUID())) {
                log.warning(
                    "A player was found in the RPEngine database with a different unique ID " +
                        "than a player that is currently logged in (offline mode?). " +
                        "Using the existing player."
                );
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
                r.getError().ifPresent(log::warning)
            );
        }

        return Result.ok(newPlayer);
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

    private <T> Result<Void> handleAndReloadPlayers(Result<T> r) {
        Optional<String> err = handleResult(() -> r).getError();
        return err
            .<Result<Void>>map(Result::error)
            .orElseGet(this::reloadPlayers);

    }

    private void updatePlayer(RpPlayer player) {
        players.put(player.getUUID(), player);
        playerNameMap.put(player.getUsername(), player);
    }

    private Result<RpPlayer> reloadPlayer(UUID playerId) {
        Result<RpPlayer> updatedPlayer = playerDbo.selectPlayer(playerId);

        Optional<String> err = updatedPlayer.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        RpPlayer p = updatedPlayer.get();
        updatePlayer(p);

        return Result.ok(p);
    }

    private void onLoad(Set<RpPlayer> loadedPlayers) {
        players = Collections.synchronizedMap(loadedPlayers.stream()
            .collect(Collectors.toMap(RpPlayer::getUUID, Function.identity())));
        playerNameMap = Collections.synchronizedMap(loadedPlayers.stream()
            .collect(Collectors.toMap(RpPlayer::getUsername, Function.identity())));
    }

    private Optional<ChatColor> getMarkerColor(RpPlayer player) {
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
            .flatMap(select -> select.getOption(optionId))
            .flatMap(Option::getColor);
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
