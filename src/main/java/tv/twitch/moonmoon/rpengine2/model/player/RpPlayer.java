package tv.twitch.moonmoon.rpengine2.model.player;

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

    public RpPlayer(
        int id,
        Instant created,
        String username,
        UUID uuid,
        Set<RpPlayerAttribute> attributes
    ) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.username = Objects.requireNonNull(username);
        this.uuid = Objects.requireNonNull(uuid);
        this.attributes = Objects.requireNonNull(attributes).stream()
            .collect(Collectors.toMap(RpPlayerAttribute::getAttributeId, Function.identity()));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpPlayer rpPlayer = (RpPlayer) o;
        return id == rpPlayer.id &&
            Objects.equals(created, rpPlayer.created) &&
            Objects.equals(username, rpPlayer.username) &&
            Objects.equals(uuid, rpPlayer.uuid) &&
            Objects.equals(attributes, rpPlayer.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "RpPlayer{" +
            "id=" + id +
            ", created=" + created +
            ", username='" + username + '\'' +
            ", uuid=" + uuid +
            ", attributes=" + attributes +
            '}';
    }
}
