package tv.twitch.moonmoon.rpengine2.countdown;

import java.util.Set;
import java.util.UUID;

public interface CountdownFactory {

    Countdown newInstance(Set<UUID> playerIds, int timeSecs);
}
