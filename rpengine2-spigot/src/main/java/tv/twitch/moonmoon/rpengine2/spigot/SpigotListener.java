package tv.twitch.moonmoon.rpengine2.spigot;

import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tv.twitch.moonmoon.rpengine2.Engine;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class SpigotListener implements Listener {

    private final Engine engine;
    private final RpPlayerRepo playerRepo;
    private final Logger log;

    @Inject
    public SpigotListener(Engine engine, RpPlayerRepo playerRepo, @PluginLogger Logger log) {
        this.engine = Objects.requireNonNull(engine);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.log = Objects.requireNonNull(log);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        getPlayer(e.getPlayer()).ifPresent(engine::handlePlayerJoined);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        getPlayer(e.getPlayer()).ifPresent(engine::handlePlayerQuit);
    }

    private Optional<RpPlayer> getPlayer(Player player) {
        Result<RpPlayer> p = playerRepo.getPlayer(player.getUniqueId());

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            return Optional.empty();
        }

        return Optional.of(p.get());
    }
}
