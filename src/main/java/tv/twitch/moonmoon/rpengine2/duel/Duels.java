package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.UUID;

public interface Duels {

    void startDuel(RpPlayer p1, RpPlayer p2);

    void endDuel(Duel duel, Dueler winner, Dueler loser);

    Optional<Duel> getActiveDuel(UUID playerId);

    void forfeitDuel(RpPlayer player);

    void handlePlayerJoined(RpPlayer player);

    Result<Void> init();
}
