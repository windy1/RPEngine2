package tv.twitch.moonmoon.rpengine2.model.player;

import tv.twitch.moonmoon.rpengine2.model.Model;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class RpPlayerAttribute implements Model {

    private final int id;
    private final int attributeId;
    private final Instant created;
    private final AttributeType type;
    private final String name;
    private final Object value;

    public RpPlayerAttribute(
        int id,
        int attributeId,
        Instant created,
        AttributeType type,
        String name,
        Object value
    ) {
        this.id = id;
        this.attributeId = attributeId;
        this.created = Objects.requireNonNull(created);
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.value = value;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getAttributeId() {
        return attributeId;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public Optional<Object> getValue() {
        return Optional.ofNullable(value);
    }

    public AttributeType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpPlayerAttribute that = (RpPlayerAttribute) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "RpPlayerAttribute{" +
            "id=" + id +
            ", attributeId=" + attributeId +
            ", created=" + created +
            ", type=" + type +
            ", name='" + name + '\'' +
            ", value=" + value +
            '}';
    }
}
