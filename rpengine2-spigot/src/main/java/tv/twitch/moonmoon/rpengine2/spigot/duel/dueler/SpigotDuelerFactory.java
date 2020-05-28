package tv.twitch.moonmoon.rpengine2.spigot.duel.dueler;

import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;

public class SpigotDuelerFactory implements DuelerFactory {

    @Inject
    public SpigotDuelerFactory() {
    }

    @Override
    public Dueler newInstance(RpPlayer p) {
        return new SpigotDueler(p);
    }
}
