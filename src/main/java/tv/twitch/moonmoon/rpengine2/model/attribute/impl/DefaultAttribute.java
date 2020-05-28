package tv.twitch.moonmoon.rpengine2.model.attribute.impl;

import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class DefaultAttribute implements Attribute {

    private final int id;
    private final Instant created;
    private final String name;
    private final String display;
    private final AttributeType type;
    private final Object defaultValue;
    private final String formatString;
    private final boolean identity;
    private final boolean marker;
    private final boolean title;

    public DefaultAttribute(
        int id,
        Instant created,
        String name,
        String display,
        AttributeType type,
        Object defaultValue,
        String formatString,
        boolean identity,
        boolean marker,
        boolean title
    ) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.name = Objects.requireNonNull(name);
        this.display = Objects.requireNonNull(display);
        this.type = Objects.requireNonNull(type);
        this.defaultValue = defaultValue;
        this.formatString = formatString;
        this.identity = identity;
        this.marker = marker;
        this.title = title;
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
    public String getName() {
        return name;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public AttributeType getType() {
        return type;
    }

    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Override
    public Optional<String> getFormatString() {
        return Optional.ofNullable(formatString);
    }

    @Override
    public boolean isIdentity() {
        return identity;
    }

    @Override
    public boolean isMarker() {
        return marker;
    }

    @Override
    public boolean isTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultAttribute attribute = (DefaultAttribute) o;
        return Objects.equals(name, attribute.name);
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
            ", identity=" + identity +
            ", marker=" + marker +
            '}';
    }
}
