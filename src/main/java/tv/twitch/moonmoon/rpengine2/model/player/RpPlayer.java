package tv.twitch.moonmoon.rpengine2.model.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RpPlayer {

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

    public int getId() {
        return id;
    }

    public Instant getCreated() {
        return created;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Set<RpPlayerAttribute> getAttributes() {
        return Collections.unmodifiableSet(new HashSet<>(attributes.values()));
    }

    public Optional<RpPlayerAttribute> getAttribute(int attributeId) {
        return Optional.ofNullable(attributes.get(attributeId));
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    public Duration getPlayed() {
        return played;
    }

    public Duration getPlayedLive() {
        if (sessionStart == null) {
            return played;
        } else {
            return played.plus(Duration.between(sessionStart, Instant.now()));
        }
    }

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
