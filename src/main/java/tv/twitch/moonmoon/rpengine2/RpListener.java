package tv.twitch.moonmoon.rpengine2;

import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import java.util.Objects;

public class RpListener implements Listener {

    private final RpPlayerRepo playerRepo;

    @Inject
    public RpListener(RpPlayerRepo playerRepo) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerRepo.handlePlayerJoined(e.getPlayer());
    }
}
