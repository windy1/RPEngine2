package tv.twitch.moonmoon.rpengine2.model.attribute;

import tv.twitch.moonmoon.rpengine2.model.Model;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * An attribute is a key-value pair that describes something about a
 * {@link tv.twitch.moonmoon.rpengine2.model.player.RpPlayer}. Values may be one of the
 * {@link AttributeType}s.
 */
public class Attribute implements Model {

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

    public Attribute(
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

    /**
     * Returns this Attribute's unique name
     *
     * @return Attribute name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this Attributes display name
     *
     * @return Display name
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Returns this attributes {@link AttributeType}
     *
     * @return Attribute type
     */
    public AttributeType getType() {
        return type;
    }

    /**
     * Returns the default value for this attribute or empty if none
     *
     * @return Default value
     */
    public Optional<Object> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    /**
     * Returns the format string for this attribute or empty if none
     *
     * @return Format string
     */
    public Optional<String> getFormatString() {
        return Optional.ofNullable(formatString);
    }

    /**
     * Returns true if this is the identity attribute
     *
     * @return True if identity attribute
     */
    public boolean isIdentity() {
        return identity;
    }

    /**
     * Returns true if this is the marker attribute
     *
     * @return true if marker attribute
     */
    public boolean isMarker() {
        return marker;
    }

    /**
     * Returns true if this is the title attribute
     *
     * @return True if title attribute
     */
    public boolean isTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
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
