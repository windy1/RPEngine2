package tv.twitch.moonmoon.rpengine2.sponge.duel.dueler;

import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;

public class SpongeDuelerFactory implements DuelerFactory {

    @Inject
    public SpongeDuelerFactory() {
    }

    @Override
    public Dueler newInstance(RpPlayer p) {
        return new SpongeDueler(p);
    }
}
