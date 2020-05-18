package tv.twitch.moonmoon.rpengine2.nte;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.INametagApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class NametagEditPluginImpl implements NametagEditPlugin {

    private final Plugin plugin;
    private final NametagEditListener listener;

    @Inject
    public NametagEditPluginImpl(Plugin plugin, NametagEditListener listener) {
        this.plugin = Objects.requireNonNull(plugin);
        this.listener = Objects.requireNonNull(listener);
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    static class NametagEditListener implements Listener {

        private final RpPlayerRepo playerRepo;
        private final Logger log;

        @Inject
        public NametagEditListener(RpPlayerRepo playerRepo, @PluginLogger Logger log) {
            this.playerRepo = Objects.requireNonNull(playerRepo);
            this.log = Objects.requireNonNull(log);
        }

        @EventHandler(priority = EventPriority.LOW)
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

            INametagApi ntApi = NametagEdit.getApi();
            if (ntApi != null) {
                ntApi.setPrefix(playerRepo.getIdentityPlain(player), playerRepo.getPrefix(player));
            }
        }
    }
}
