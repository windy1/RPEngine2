package tv.twitch.moonmoon.rpengine2.data.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class RpPlayerRepoImpl implements RpPlayerRepo {

    private final Plugin plugin;
    private final RpPlayerDbo playerDbo;
    private final AttributeRepo attributeRepo;
    private final Logger log;
    private Map<String, RpPlayer> players;

    private final Queue<OfflinePlayer> joinedPlayers = new ConcurrentLinkedQueue<>();

    @Inject
    public RpPlayerRepoImpl(Plugin plugin, RpPlayerDbo playerDbo, AttributeRepo attributeRepo) {
        this.plugin = Objects.requireNonNull(plugin);
        this.playerDbo = Objects.requireNonNull(playerDbo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        log = plugin.getLogger();
    }

    @Override
    public Set<RpPlayer> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

    @Override
    public Result<RpPlayer> getPlayer(OfflinePlayer player) {
        Objects.requireNonNull(player);
        return Optional.ofNullable(players.get(player.getUniqueId().toString()))
            .map(Result::ok)
            .orElseGet(() -> handleResult(() -> createPlayer(player)));
    }

    @Override
    public Optional<RpPlayer> getPlayer(String name) {
        for (RpPlayer player : players.values()) {
            if (player.getUsername().equals(name)) {
                return Optional.of(player);
            }
        }

        return Optional.empty();
    }

    @Override
    public void setAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Consumer<Result<Void>> callback
    ) {
        // try to insert the attribute
        addAttributeAsync(player, attributeId, value, r -> {
            Result<Boolean> result = handleResult(() -> r);

            Optional<String> err = result.getError();
            if (err.isPresent()) {
                // insertion failed
                callback.accept(Result.error(err.get()));
                return;
            }

            if (result.get()) {
                // attribute inserted
                callback.accept(handleReloadPlayer(player.getUUID()));
                return;
            }

            // attribute already exists, update required
            Result<Void> updateResult = handleResult(() ->
                playerDbo.updatePlayerAttribute(player.getId(), attributeId, value)
            );

            err = updateResult.getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
            } else {
                callback.accept(handleReloadPlayer(player.getUUID()));
            }
        });
    }

    @Override
    public void removeAttributesAsync(int attributeId, Consumer<Result<Void>> callback) {
        playerDbo.deletePlayerAttributesAsync(attributeId, r -> {
            // delete attributes
            Optional<String> err = handleResult(() -> r).getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
                return;
            }

            // reload players
            Result<Set<RpPlayer>> reloadedPlayers = handleResult(playerDbo::selectPlayers);

            err = reloadedPlayers.getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
            } else {
                onLoad(reloadedPlayers.get());
                callback.accept(Result.ok(null));
            }
        });
    }

    @Override
    public void load() {
        playerDbo.selectPlayersAsync(r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
                return;
            }

            onLoad(r.get());
            startJoinedPlayersWatcher();
        });
    }

    private void onLoad(Set<RpPlayer> loadedPlayers) {
        players = loadedPlayers.stream()
            .collect(Collectors.toMap(p -> p.getUUID().toString(), Function.identity()));
        log.info("loaded players " + players);
    }

    @Override
    public void handlePlayerJoined(OfflinePlayer player) {
        joinedPlayers.add(player);
    }

    @Override
    public void flushJoinedPlayers() {
        synchronized (joinedPlayers) {
            while (!joinedPlayers.isEmpty()) {
                createPlayer(joinedPlayers.poll()).getError()
                    .ifPresent(log::info);
            }
        }
    }

    private void addAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Consumer<Result<Boolean>> callback
    ) {
        playerDbo.insertPlayerAttributeAsync(player.getId(), attributeId, value, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                // insertion failed
                callback.accept(Result.error(err.get()));
                return;
            }

            // inserted or ignored
            long playerAttributeId = r.get();

            callback.accept(Result.ok(playerAttributeId != 0));
        });
    }

    private void startJoinedPlayersWatcher() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            plugin,
            this::flushJoinedPlayers,
            0, 20
        );
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
            return Result.ok(players.get(playerId.toString()));
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

    private Result<RpPlayer> reloadPlayer(UUID playerId) {
        Result<RpPlayer> updatedPlayer = playerDbo.selectPlayer(playerId);

        Optional<String> err = updatedPlayer.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        RpPlayer p = updatedPlayer.get();
        players.put(playerId.toString(), p);

        return Result.ok(p);
    }

    private Result<Void> handleReloadPlayer(UUID playerId) {
        return handleResult(() -> reloadPlayer(playerId).mapOk(p -> null));
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
