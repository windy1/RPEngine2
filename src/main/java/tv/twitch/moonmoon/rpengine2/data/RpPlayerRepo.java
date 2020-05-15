package tv.twitch.moonmoon.rpengine2.data;

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
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class RpPlayerRepo {

    private final Plugin plugin;
    private final RpDb db;
    private final Logger log;
    private Map<String, RpPlayer> players;

    private final Queue<OfflinePlayer> joinedPlayers = new ConcurrentLinkedQueue<>();

    @Inject
    public RpPlayerRepo(Plugin plugin, RpDb db) {
        this.plugin = Objects.requireNonNull(plugin);
        this.db = Objects.requireNonNull(db);
        log = plugin.getLogger();
    }

    public Set<RpPlayer> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

    public Result<RpPlayer> getPlayer(Player player) {
        RpPlayer p = players.get(player.getUniqueId().toString());
        if (p == null) {
            Result<RpPlayer> r = createPlayer(player);

            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.info(err.get());
                return Result.error(StringUtils.GENERIC_ERROR);
            } else {
                return r;
            }
        } else {
            return Result.ok(p);
        }
    }

    public void setAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Consumer<Result<Void>> callback
    ) {
        db.insertPlayerAttributeAsync(player.getId(), attributeId, value, callback);
    }

    public void load() {
        db.selectPlayersAsync(r -> {
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

    public void startJoinedPlayersWatcher() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            plugin,
            this::flushJoinedPlayers,
            0, 20
        );
    }

    public void flushJoinedPlayers() {
        synchronized (joinedPlayers) {
            while (!joinedPlayers.isEmpty()) {
                createPlayer(joinedPlayers.poll()).getError()
                    .ifPresent(log::info);
            }
        }
    }

    private Result<RpPlayer> createPlayer(OfflinePlayer player) {
        db.insertPlayer(player).getError().ifPresent(log::warning);

        Result<RpPlayer> newPlayer = db.selectPlayer(player.getUniqueId());

        Optional<String> err = newPlayer.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        RpPlayer p = newPlayer.get();
        players.put(p.getUUID().toString(), p);

        return Result.ok(p);
    }
}
