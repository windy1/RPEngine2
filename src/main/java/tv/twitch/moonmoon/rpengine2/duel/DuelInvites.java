package tv.twitch.moonmoon.rpengine2.duel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Manages pending challenges to duel
 */
@Singleton
public class DuelInvites {

    private final Plugin plugin;
    private final Map<UUID, Set<DuelInvite>> invites =
        Collections.synchronizedMap(new HashMap<>());

    private BukkitTask task;

    @Inject
    public DuelInvites(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    /**
     * Returns true if the specified player has an invite from the specified target
     *
     * @param playerId Player ID
     * @param targetId Player ID
     * @return True if has invite
     */
    public boolean has(UUID playerId, UUID targetId) {
        return getInvites(playerId).contains(new DuelInvite(targetId));
    }

    /**
     * Clears all invites for the specified player
     *
     * @param playerId Player ID
     */
    public void clear(UUID playerId) {
        getInvites(playerId).clear();
    }

    /**
     * Adds an invite for the specified players
     *
     * @param playerId Invitee
     * @param targetId Inviter
     */
    public void add(UUID playerId, UUID targetId) {
        getInvites(playerId).add(new DuelInvite(targetId));
    }

    /**
     * Removes an invite
     *
     * @param playerId Player ID
     * @param targetId Player ID
     * @return True if invite was present
     */
    public boolean decline(UUID playerId, UUID targetId) {
        return getInvites(playerId).remove(new DuelInvite(targetId));
    }

    void startWatching() {
        int inviteExpireSecs = plugin.getConfig().getInt("duels.inviteExpireSecs", 300);
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            synchronized (invites) {
                pruneExpiredInvites(inviteExpireSecs);
            }
        }, 0, 10);
    }

    private void pruneExpiredInvites(int inviteExpireSecs) {
        for (Map.Entry<UUID, Set<DuelInvite>> entry : invites.entrySet()) {
            Iterator<DuelInvite> inviteIt = entry.getValue().iterator();

            while (inviteIt.hasNext()) {
                DuelInvite invite = inviteIt.next();
                Instant now = Instant.now();
                long elapsedSecs = Duration.between(invite.invitedAt, now).toMillis() / 1000;

                if (elapsedSecs <= inviteExpireSecs) {
                    continue;
                }

                Player player = Bukkit.getPlayer(invite.playerId);
                OfflinePlayer target = Bukkit.getOfflinePlayer(entry.getKey());
                if (player != null) {
                    player.sendMessage(
                        ChatColor.RED + "Your request to duel " + target.getName()
                            + " has expired"
                    );
                }

                inviteIt.remove();
            }
        }
    }

    private Set<DuelInvite> getInvites(UUID invitedPlayerId) {
        return invites.computeIfAbsent(invitedPlayerId, k ->
            Collections.synchronizedSet(new HashSet<>())
        );
    }

    @Override
    protected void finalize() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    static class DuelInvite {

        final UUID playerId;
        final Instant invitedAt;

        DuelInvite(UUID playerId) {
            this.playerId = playerId;
            invitedAt = Instant.now();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DuelInvite that = (DuelInvite) o;
            return Objects.equals(playerId, that.playerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerId);
        }

        @Override
        public String toString() {
            return "DuelInvite{" +
                "playerId=" + playerId +
                ", invitedAt=" + invitedAt +
                '}';
        }
    }
}
