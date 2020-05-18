package tv.twitch.moonmoon.rpengine2;

import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Objects;
import java.util.Optional;
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
        Player mcPlayer = e.getPlayer();
        Result<RpPlayer> p = playerRepo.getPlayer(mcPlayer);
        RpPlayer player;

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            return;
        }
        player = p.get();

//        INametagApi ntApi = NametagEdit.getApi();
//        if (ntApi != null) {
//            ntApi.setPrefix(mcPlayer, playerRepo.getPrefix(player));
//        }
    }
}
