package tv.twitch.moonmoon.rpengine2.spigot.model.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.player.impl.DefaultRpPlayer;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SpigotRpPlayer extends DefaultRpPlayer {

    public SpigotRpPlayer(
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

    /**
     * Returns the Bukkit {@link Player} if found, empty otherwise
     *
     * @return Bukkit player
     */
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }
}
