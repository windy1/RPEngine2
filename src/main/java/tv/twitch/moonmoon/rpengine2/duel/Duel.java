package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;

import java.time.Instant;
import java.util.Optional;

/**
 * Duel tracking data
 */
public interface Duel {

    /**
     * Returns player 1
     *
     * @return Player 1
     */
    Dueler getPlayer1();

    /**
     * Return player 2
     *
     * @return Player 2
     */
    Dueler getPlayer2();

    /**
     * Returns true if the duel has started
     *
     * @return True if duel has started
     */
    boolean hasStarted();

    /**
     * Marks the duel as started
     */
    void start();

    /**
     * Returns the {@link Instant} this duel was started
     *
     * @return Instant duel was started if found
     */
    Optional<Instant> getStartTime();
}
