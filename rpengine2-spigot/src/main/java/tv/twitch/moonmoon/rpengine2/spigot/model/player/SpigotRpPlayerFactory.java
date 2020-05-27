package tv.twitch.moonmoon.rpengine2.spigot.model.player;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class SpigotRpPlayerFactory implements RpPlayerFactory {

    @Inject
    public SpigotRpPlayerFactory() {
    }

    @Override
    public RpPlayer newInstance(
        int playerId,
        Instant created,
        String username,
        UUID uuid,
        Set<RpPlayerAttribute> attributes,
        Duration played,
        Instant sessionStart
    ) {
        return new SpigotRpPlayer(
            playerId,
            created,
            username,
            uuid,
            attributes,
            played,
            sessionStart
        );
    }
}
