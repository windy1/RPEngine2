package tv.twitch.moonmoon.rpengine2.model.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.model.Model;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a plugin-tracker player
 */
public class RpPlayer implements Model {

    private final int id;
    private final Instant created;
    private final String username;
    private final UUID uuid;
    private final Map<Integer, RpPlayerAttribute> attributes;
    private final Duration played;
    private final Instant sessionStart;

    public RpPlayer(
        int id,
        Instant created,
        String username,
        UUID uuid,
        Set<RpPlayerAttribute> attributes,
        Duration played,
        Instant sessionStart
    ) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.username = Objects.requireNonNull(username);
        this.uuid = Objects.requireNonNull(uuid);
        this.attributes = Objects.requireNonNull(attributes).stream()
            .collect(Collectors.toMap(RpPlayerAttribute::getAttributeId, Function.identity()));
        this.played = Objects.requireNonNull(played);
        this.sessionStart = sessionStart;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    /**
     * Returns the player's Mojang username
     *
     * @return Mojang name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the player's Mojang {@link UUID}
     *
     * @return Mojang UUID
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns a set of this player's attributes
     *
     * @return Player attributes
     */
    public Set<RpPlayerAttribute> getAttributes() {
        return Collections.unmodifiableSet(new HashSet<>(attributes.values()));
    }

    /**
     * Returns the attribute for this player with the specified ID
     *
     * @param attributeId Attribute to get
     * @return Attribute if found, empty otherwise
     */
    public Optional<RpPlayerAttribute> getAttribute(int attributeId) {
        return Optional.ofNullable(attributes.get(attributeId));
    }

    /**
     * Returns the Bukkit {@link Player} if found, empty otherwise
     *
     * @return Bukkit player
     */
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    /**
     * Returns the {@link Duration} that this player has logged, not including the current session
     *
     * @return Play time
     */
    public Duration getPlayed() {
        return played;
    }

    /**
     * Returns the {@link Duration} that this player has logged, including the current session
     *
     * @return Play time
     */
    public Duration getPlayedLive() {
        if (sessionStart == null) {
            return played;
        } else {
            return played.plus(Duration.between(sessionStart, Instant.now()));
        }
    }

    /**
     * Returns the {@link Instant} this player's current session started or empty if
     * offline. However, this should not be relied upon to determine if a player is online.
     *
     * @return Instant session started
     */
    public Optional<Instant> getSessionStart() {
        return Optional.ofNullable(sessionStart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpPlayer rpPlayer = (RpPlayer) o;
        return Objects.equals(username, rpPlayer.username) &&
            Objects.equals(uuid, rpPlayer.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, uuid);
    }

    @Override
    public String toString() {
        return "RpPlayer{" +
            "id=" + id +
            ", created=" + created +
            ", username='" + username + '\'' +
            ", uuid=" + uuid +
            ", attributes=" + attributes +
            ", played=" + (played.toMillis() / 1000) +
            ", sessionStart=" + sessionStart +
            '}';
    }
}
