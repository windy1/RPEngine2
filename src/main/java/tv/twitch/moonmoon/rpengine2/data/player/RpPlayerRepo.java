package tv.twitch.moonmoon.rpengine2.data.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class RpPlayerRepo {

    private final Plugin plugin;
    private final PlayerDbo playerDbo;
    private final Logger log;
    private Map<String, RpPlayer> players;

    private final Queue<OfflinePlayer> joinedPlayers = new ConcurrentLinkedQueue<>();

    @Inject
    public RpPlayerRepo(Plugin plugin, PlayerDbo playerDbo) {
        this.plugin = Objects.requireNonNull(plugin);
        this.playerDbo = Objects.requireNonNull(playerDbo);
        log = plugin.getLogger();
    }

    public Set<RpPlayer> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

    public Result<RpPlayer> getPlayer(Player player) {
        Objects.requireNonNull(player);
        return Optional.ofNullable(players.get(player.getUniqueId().toString()))
            .map(Result::ok)
            .orElseGet(() -> handleResult(() -> createPlayer(player)));
    }

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

            if (r.get()) {
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

    public void load() {
        playerDbo.selectPlayersAsync(r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
                return;
            }

            players = r.get().stream()
                .collect(Collectors.toMap(p -> p.getUUID().toString(), Function.identity()));

            log.info("loaded players " + players);

            startJoinedPlayersWatcher();
        });
    }

    public void handlePlayerJoined(OfflinePlayer player) {
        joinedPlayers.add(player);
    }

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
            callback.accept(Result.ok(playerAttributeId != -1));
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

        if (newPlayerId.get() == -1) {
            // player already existed
            return Result.ok(players.get(playerId.toString()));
        }

        // TODO: insert attributes

        return reloadPlayer(playerId);
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

    private <T> Result<T> handleResult(Supplier<Result<T>> f) {
        Result<T> r = f.get();
        Optional<String> err = r.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            return Result.error(StringUtils.GENERIC_ERROR);
        } else {
            return r;
        }
    }

    private Result<Void> handleReloadPlayer(UUID playerId) {
        return handleResult(() -> reloadPlayer(playerId).mapOk(p -> null));
    }
}
