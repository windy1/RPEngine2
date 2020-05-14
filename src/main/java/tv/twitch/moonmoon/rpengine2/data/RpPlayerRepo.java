package tv.twitch.moonmoon.rpengine2.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.Result;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

@Singleton
public class RpPlayerRepo {

    private final Plugin plugin;
    private final RpDb db;
    private final Logger log;
    private Set<RpPlayer> players;

    private final Queue<OfflinePlayer> joinedPlayers = new ConcurrentLinkedQueue<>();

    @Inject
    public RpPlayerRepo(Plugin plugin, RpDb db) {
        this.plugin = Objects.requireNonNull(plugin);
        this.db = Objects.requireNonNull(db);
        log = plugin.getLogger();
    }

    public void load() {
        db.selectPlayersAsync(r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
                return;
            }

            players = r.get();
            log.info("loaded players " + players);

            startJoinedPlayersWatcher();
        });
    }

    public void handlePlayerJoined(OfflinePlayer player) {
        joinedPlayers.add(player);
    }

    public void startJoinedPlayersWatcher() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            while (!joinedPlayers.isEmpty()) {
                OfflinePlayer player = joinedPlayers.poll();

                db.insertPlayer(player).getError().ifPresent(log::warning);

                Result<RpPlayer> newPlayer = db.selectPlayer(player.getUniqueId());

                Optional<String> err = newPlayer.getError();
                if (err.isPresent()) {
                    log.warning(err.get());
                } else {
                    players.add(newPlayer.get());
                }
            }
        }, 0, 20);
    }
}
