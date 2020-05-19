package tv.twitch.moonmoon.rpengine2.duel;

import java.util.Objects;

public class Duel {

    private final Dueler player1;
    private final Dueler player2;

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
}
