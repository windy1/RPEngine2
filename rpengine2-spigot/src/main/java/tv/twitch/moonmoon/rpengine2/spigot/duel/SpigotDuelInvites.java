package tv.twitch.moonmoon.rpengine2.spigot.duel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import tv.twitch.moonmoon.rpengine2.duel.AbstractDuelInvites;
import tv.twitch.moonmoon.rpengine2.util.Lang;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Manages pending challenges to duel
 */
@Singleton
public class SpigotDuelInvites extends AbstractDuelInvites {

    private final Plugin plugin;
    private BukkitTask task;

    @Inject
    public SpigotDuelInvites(Plugin plugin) {
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
    protected void onInviteExpired(UUID playerId, UUID targetId) {
        Player player = Bukkit.getPlayer(playerId);
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetId);
        if (player != null) {
            player.sendMessage(
                ChatColor.RED + Lang.getString("duels.inviteExpired", target.getName())
            );
        }
    }

    @Override
    protected void finalize() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}
