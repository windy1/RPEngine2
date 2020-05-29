package tv.twitch.moonmoon.rpengine2.duel.impl;

import tv.twitch.moonmoon.rpengine2.Config;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.task.Task;
import tv.twitch.moonmoon.rpengine2.task.TaskFactory;
import tv.twitch.moonmoon.rpengine2.util.Lang;
import tv.twitch.moonmoon.rpengine2.util.Messenger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Singleton
public class DefaultDuelInvites implements DuelInvites {

    private final Map<UUID, Set<DuelInvite>> invites =
        Collections.synchronizedMap(new HashMap<>());
    private final Messenger messenger;
    private final RpPlayerRepo playerRepo;
    private final Config config;
    private final TaskFactory taskFactory;

    private Task task;

    @Inject
    public DefaultDuelInvites(
        Messenger messenger,
        RpPlayerRepo playerRepo,
        Config config,
        TaskFactory taskFactory
    ) {
        this.messenger = Objects.requireNonNull(messenger);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.config = Objects.requireNonNull(config);
        this.taskFactory = Objects.requireNonNull(taskFactory);
    }

    @Override
    public boolean has(UUID playerId, UUID targetId) {
        return getInvites(playerId).contains(new DuelInvite(targetId));
    }

    @Override
    public void clear(UUID playerId) {
        getInvites(playerId).clear();
    }

    @Override
    public void add(UUID playerId, UUID targetId) {
        getInvites(playerId).add(new DuelInvite(targetId));
    }

    @Override
    public boolean decline(UUID playerId, UUID targetId) {
        return getInvites(playerId).remove(new DuelInvite(targetId));
    }

    @Override
    public void startWatching() {
        int inviteExpireSecs = config.getInt("duels.inviteExpireSecs", 300);
        task = taskFactory.newInstance();
        task.setIntervalAsync(
            () -> pruneExpiredInvites(inviteExpireSecs),
            0, 500
        );
    }

    @Override
    protected void finalize() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    private Set<DuelInvite> getInvites(UUID invitedPlayerId) {
        return invites.computeIfAbsent(invitedPlayerId, k ->
            Collections.synchronizedSet(new HashSet<>())
        );
    }

    private void pruneExpiredInvites(int inviteExpireSecs) {
        synchronized (invites) {
            for (Map.Entry<UUID, Set<DuelInvite>> entry : invites.entrySet()) {
                Iterator<DuelInvite> inviteIt = entry.getValue().iterator();

                while (inviteIt.hasNext()) {
                    DuelInvite invite = inviteIt.next();
                    Instant now = Instant.now();
                    long elapsedSecs = Duration.between(invite.invitedAt, now).toMillis() / 1000;

                    if (elapsedSecs <= inviteExpireSecs) {
                        continue;
                    }

                    RpPlayer player = playerRepo.getPlayer(invite.playerId).orElse(null);
                    RpPlayer target = playerRepo.getPlayer(entry.getKey()).orElse(null);

                    if (player != null) {
                        String message = Lang.getString(
                            "duels.inviteExpired", target.getUsername()
                        );
                        messenger.sendError(player, message);
                    }

                    inviteIt.remove();
                }
            }
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
