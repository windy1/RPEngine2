package tv.twitch.moonmoon.rpengine2.duel;

import java.util.UUID;

public interface DuelInvites {
    /**
     * Returns true if the specified player has an invite from the specified target
     *
     * @param playerId Player ID
     * @param targetId Player ID
     * @return True if has invite
     */
    boolean has(UUID playerId, UUID targetId);

    /**
     * Clears all invites for the specified player
     *
     * @param playerId Player ID
     */
    void clear(UUID playerId);

    /**
     * Adds an invite for the specified players
     *
     * @param playerId Invitee
     * @param targetId Inviter
     */
    void add(UUID playerId, UUID targetId);

    /**
     * Removes an invite
     *
     * @param playerId Player ID
     * @param targetId Player ID
     * @return True if invite was present
     */
    boolean decline(UUID playerId, UUID targetId);

    void startWatching();
}
