package tv.twitch.moonmoon.rpengine2.duel.impl;

import tv.twitch.moonmoon.rpengine2.duel.Duel;
import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class DefaultDuel implements Duel {

    private final Dueler player1;
    private final Dueler player2;
    private Instant startTime;

    public DefaultDuel(Dueler player1, Dueler player2) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
    }

    @Override
    public Dueler getPlayer1() {
        return player1;
    }

    @Override
    public Dueler getPlayer2() {
        return player2;
    }

    @Override
    public boolean hasStarted() {
        return startTime != null;
    }

    @Override
    public void start() {
        startTime = Instant.now();
    }

    @Override
    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultDuel duel = (DefaultDuel) o;
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
