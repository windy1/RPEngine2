package tv.twitch.moonmoon.rpengine2.sponge.countdown;

import tv.twitch.moonmoon.rpengine2.countdown.Countdown;
import tv.twitch.moonmoon.rpengine2.countdown.CountdownFactory;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;

public class SpongeCountdownFactory implements CountdownFactory {

    @Inject
    public SpongeCountdownFactory() {
    }

    @Override
    public Countdown newInstance(Set<UUID> playerIds, int timeSecs) {
        // TODO
        return new SpongeCountdown();
    }
}
