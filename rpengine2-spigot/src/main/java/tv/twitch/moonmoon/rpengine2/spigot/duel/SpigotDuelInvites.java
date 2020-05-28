package tv.twitch.moonmoon.rpengine2.spigot.duel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.impl.AbstractDuelInvites;
import tv.twitch.moonmoon.rpengine2.util.Messenger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

/**
 * Manages pending challenges to duel
 */
@Singleton
public class SpigotDuelInvites extends AbstractDuelInvites {

    private final Plugin plugin;
    private BukkitTask task;

    @Inject
    public SpigotDuelInvites(Plugin plugin, Messenger messenger, RpPlayerRepo playerRepo) {
        super(messenger, playerRepo);
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void startWatching() {
        int inviteExpireSecs = plugin.getConfig().getInt("duels.inviteExpireSecs", 300);
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () ->
            pruneExpiredInvites(inviteExpireSecs),
            0, 10
        );
    }

    @Override
    protected void finalize() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}
