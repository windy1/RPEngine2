package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

public interface Duels {

    void startDuel(RpPlayer p1, RpPlayer p2);

    Result<Void> init();
}
