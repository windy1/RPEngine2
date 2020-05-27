package tv.twitch.moonmoon.rpengine2.duel;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public abstract class AbstractDuelInvites implements DuelInvites {

    protected final Map<UUID, Set<DuelInvite>> invites =
        Collections.synchronizedMap(new HashMap<>());

    protected abstract void onInviteExpired(UUID playerId, UUID targetId);

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

    private Set<DuelInvite> getInvites(UUID invitedPlayerId) {
        return invites.computeIfAbsent(invitedPlayerId, k ->
            Collections.synchronizedSet(new HashSet<>())
        );
    }

    protected void pruneExpiredInvites(int inviteExpireSecs) {
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

                    onInviteExpired(invite.playerId, entry.getKey());

                    inviteIt.remove();
                }
            }
        }
    }

    protected static class DuelInvite {

        public final UUID playerId;
        public final Instant invitedAt;

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
