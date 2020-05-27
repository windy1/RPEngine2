package tv.twitch.moonmoon.rpengine2.sponge.model.player;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import tv.twitch.moonmoon.rpengine2.model.player.CoreRpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SpongeRpPlayer extends CoreRpPlayer {

    public SpongeRpPlayer(
        int id,
        Instant created,
        String username,
        UUID uuid,
        Set<RpPlayerAttribute> attributes,
        Duration played,
        Instant sessionStart
    ) {
        super(id, created, username, uuid, attributes, played, sessionStart);
    }

    public Optional<Player> getPlayer() {
        return Sponge.getServer().getPlayer(uuid);
    }
}
