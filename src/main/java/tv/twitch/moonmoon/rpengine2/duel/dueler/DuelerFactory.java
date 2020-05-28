package tv.twitch.moonmoon.rpengine2.duel.dueler;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

public interface DuelerFactory {

    Dueler newInstance(RpPlayer p);
}
