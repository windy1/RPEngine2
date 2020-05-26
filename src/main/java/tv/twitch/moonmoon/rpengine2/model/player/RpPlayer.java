package tv.twitch.moonmoon.rpengine2.model.player;

import tv.twitch.moonmoon.rpengine2.model.Model;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RpPlayer extends Model {
    /**
     * Returns the player's Mojang username
     *
     * @return Mojang name
     */
    String getUsername();

    /**
     * Returns the player's Mojang {@link UUID}
     *
     * @return Mojang UUID
     */
    UUID getUUID();

    /**
     * Returns a set of this player's attributes
     *
     * @return Player attributes
     */
    Set<RpPlayerAttribute> getAttributes();

    /**
     * Returns the attribute for this player with the specified ID
     *
     * @param attributeId Attribute to get
     * @return Attribute if found, empty otherwise
     */
    Optional<RpPlayerAttribute> getAttribute(int attributeId);

    /**
     * Returns the {@link Duration} that this player has logged, not including the current session
     *
     * @return Play time
     */
    Duration getPlayed();

    /**
     * Returns the {@link Duration} that this player has logged, including the current session
     *
     * @return Play time
     */
    Duration getPlayedLive();

    /**
     * Returns the {@link Instant} this player's current session started or empty if
     * offline. However, this should not be relied upon to determine if a player is online.
     *
     * @return Instant session started
     */
    Optional<Instant> getSessionStart();
}
