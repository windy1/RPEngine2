package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

public interface DuelMessenger {

    void broadcastDuelEnd(Dueler winner, Dueler loser);

    void broadcastDuelTimeout(Duel duel);

    void broadcastDuelForfeit(RpPlayer winner, RpPlayer loser);
}
