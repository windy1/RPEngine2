package tv.twitch.moonmoon.rpengine2.model.player;

import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class RpPlayerAttribute {

    private final int id;
    private final int attributeId;
    private final Instant created;
    private final AttributeType type;
    private final String display;
    private final String name;
    private final Object value;

    public RpPlayerAttribute(
        int id,
        int attributeId,
        Instant created,
        AttributeType type,
        String display,
        String name,
        Object value
    ) {
        this.id = id;
        this.attributeId = attributeId;
        this.created = Objects.requireNonNull(created);
        this.type = Objects.requireNonNull(type);
        this.display = Objects.requireNonNull(display);
        this.name = Objects.requireNonNull(name);
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public Instant getCreated() {
        return created;
    }

    public String getDisplay() {
        return display;
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
        return id == that.id &&
            attributeId == that.attributeId &&
            Objects.equals(created, that.created) &&
            type == that.type &&
            Objects.equals(display, that.display) &&
            Objects.equals(name, that.name) &&
            Objects.equals(value, that.value);
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
            ", display='" + display + '\'' +
            ", name='" + name + '\'' +
            ", value=" + value +
            '}';
    }
}
