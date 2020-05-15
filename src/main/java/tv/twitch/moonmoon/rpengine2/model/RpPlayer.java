package tv.twitch.moonmoon.rpengine2.model;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class RpPlayer {

    private final int id;
    private final Instant created;
    private final String username;
    private final UUID uuid;
    private final Set<RpPlayerAttribute> attributes;
    private final Set<Integer> groupIds;

    public RpPlayer(
        int id,
        Instant created,
        String username,
        UUID uuid,
        Set<RpPlayerAttribute> attributes,
        Set<Integer> groupIds
    ) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.username = Objects.requireNonNull(username);
        this.uuid = Objects.requireNonNull(uuid);
        this.attributes = Objects.requireNonNull(attributes);
        this.groupIds = Objects.requireNonNull(groupIds);
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
        return Collections.unmodifiableSet(attributes);
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
            Objects.equals(attributes, rpPlayer.attributes) &&
            Objects.equals(groupIds, rpPlayer.groupIds);
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
            ", groupIds=" + groupIds +
            '}';
    }
}
