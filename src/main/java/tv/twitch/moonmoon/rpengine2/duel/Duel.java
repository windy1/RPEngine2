package tv.twitch.moonmoon.rpengine2.duel;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class Duel {

    private final Dueler player1;
    private final Dueler player2;
    private Instant startTime;

    public Duel(Dueler player1, Dueler player2) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
    }

    public Dueler getPlayer1() {
        return player1;
    }

    public Dueler getPlayer2() {
        return player2;
    }

    public boolean hasStarted() {
        return startTime != null;
    }

    public void start() {
        startTime = Instant.now();
    }

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
