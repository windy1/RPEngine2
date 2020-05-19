package tv.twitch.moonmoon.rpengine2.duel;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Duel tracking data
 */
public class Duel {

    private final Dueler player1;
    private final Dueler player2;
    private Instant startTime;

    public Duel(Dueler player1, Dueler player2) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
    }

    /**
     * Returns player 1
     *
     * @return Player 1
     */
    public Dueler getPlayer1() {
        return player1;
    }

    /**
     * Return player 2
     *
     * @return Player 2
     */
    public Dueler getPlayer2() {
        return player2;
    }

    /**
     * Returns true if the duel has started
     *
     * @return True if duel has started
     */
    public boolean hasStarted() {
        return startTime != null;
    }

    /**
     * Marks the duel as started
     */
    public void start() {
        startTime = Instant.now();
    }

    /**
     * Returns the {@link Instant} this duel was started
     *
     * @return Instant duel was started if found
     */
    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duel duel = (Duel) o;
        return Objects.equals(player1, duel.player1) &&
            Objects.equals(player2, duel.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player1, player2);
    }

    @Override
    public String toString() {
        return "Duel{" +
            "player1=" + player1 +
            ", player2=" + player2 +
            ", startTime=" + startTime +
            '}';
    }
}
