package tv.twitch.moonmoon.rpengine2.model.player.impl;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a plugin-tracker player
 */
public class DefaultRpPlayer implements RpPlayer {

    protected final int id;
    protected final Instant created;
    protected final String username;
    protected final UUID uuid;
    protected final Map<Integer, RpPlayerAttribute> attributes;
    protected final Duration played;
    protected final Instant sessionStart;

    public DefaultRpPlayer(
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

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Set<RpPlayerAttribute> getAttributes() {
        return Collections.unmodifiableSet(new HashSet<>(attributes.values()));
    }

    @Override
    public Optional<RpPlayerAttribute> getAttribute(int attributeId) {
        return Optional.ofNullable(attributes.get(attributeId));
    }

    @Override
    public Duration getPlayed() {
        return played;
    }

    @Override
    public Duration getPlayedLive() {
        if (sessionStart == null) {
            return played;
        } else {
            return played.plus(Duration.between(sessionStart, Instant.now()));
        }
    }

    @Override
    public Optional<Instant> getSessionStart() {
        return Optional.ofNullable(sessionStart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultRpPlayer rpPlayer = (DefaultRpPlayer) o;
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
