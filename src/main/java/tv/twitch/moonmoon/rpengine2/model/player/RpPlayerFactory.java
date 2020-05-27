package tv.twitch.moonmoon.rpengine2.model.player;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface RpPlayerFactory {
    RpPlayer newInstance(
        int playerId,
        Instant created,
        String username,
        UUID uuid,
        Set<RpPlayerAttribute> attributes,
        Duration played,
        Instant sessionStart
    );
}
