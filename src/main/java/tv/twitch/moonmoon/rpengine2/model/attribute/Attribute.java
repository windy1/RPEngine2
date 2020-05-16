package tv.twitch.moonmoon.rpengine2.model.attribute;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class Attribute {

    private final int id;
    private final Instant created;
    private final String name;
    private final String display;
    private final AttributeType type;
    private final Object defaultValue;
    private final String formatString;

    public Attribute(
        int id,
        Instant created,
        String name,
        String display,
        AttributeType type,
        Object defaultValue,
        String formatString
    ) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.name = Objects.requireNonNull(name);
        this.display = Objects.requireNonNull(display);
        this.type = Objects.requireNonNull(type);
        this.defaultValue = defaultValue;
        this.formatString = formatString;
    }

    public int getId() {
        return id;
    }

    public Instant getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public AttributeType getType() {
        return type;
    }

    public Optional<Object> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public Optional<String> getFormatString() {
        return Optional.ofNullable(formatString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return id == attribute.id &&
            Objects.equals(created, attribute.created) &&
            Objects.equals(name, attribute.name) &&
            Objects.equals(display, attribute.display) &&
            type == attribute.type &&
            Objects.equals(defaultValue, attribute.defaultValue) &&
            Objects.equals(formatString, attribute.formatString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Attribute{" +
            "id=" + id +
            ", created=" + created +
            ", name='" + name + '\'' +
            ", display='" + display + '\'' +
            ", type=" + type +
            ", defaultValue=" + defaultValue +
            ", formatString='" + formatString + '\'' +
            '}';
    }
}
