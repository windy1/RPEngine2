package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.RpModule;
import tv.twitch.moonmoon.rpengine2.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;
import tv.twitch.moonmoon.rpengine2.duel.impl.DefaultDuel;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Module responsible for managing duel-related functionality
 */
public interface Duels extends RpModule {

    /**
     * Starts a duel for the specified players
     *
     * @param p1 Player 1
     * @param p2 Player 2
     */
    void startDuel(RpPlayer p1, RpPlayer p2);

    /**
     * Ends an active duel with the specified winner and loser
     *
     * @param duel Active duel
     * @param winner Duel winner
     * @param loser Duel loser
     */
    void endDuel(Duel duel, Dueler winner, Dueler loser);

    /**
     * Returns the active duel for a player with the specified ID
     *
     * @param playerId Player ID
     * @return Active duel if found
     */
    Optional<Duel> getActiveDuel(UUID playerId);

    /**
     * Forfeits the specified player's active duel for them
     *
     * @param player Player forfeiting
     */
    void forfeitDuel(RpPlayer player);

    /**
     * Returns the {@link DuelConfigRepo} instance
     *
     * @return DuelConfigRepo instance
     */
    DuelConfigRepo getConfigRepo();

    /**
     * Returns handle to pending invites
     *
     * @return Pending invites
     */
    DuelInvites getInvites();

    /**
     * Returns a set of active {@link DefaultDuel}s
     *
     * @return Active duels
     */
    Set<Duel> getActiveDuels();

    void handlePlayerJoined(RpPlayer player);
}
