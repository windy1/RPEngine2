package tv.twitch.moonmoon.rpengine2;

import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.di.PluginLogger;

import java.util.Objects;
import java.util.logging.Logger;

public class CoreListener implements Listener {

    private final RpPlayerRepo playerRepo;
    private final Logger log;

    @Inject
    public CoreListener(RpPlayerRepo playerRepo, @PluginLogger Logger log) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.log = Objects.requireNonNull(log);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerRepo.getPlayer(e.getPlayer()).getError().ifPresent(log::warning);
    }
}
