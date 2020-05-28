package tv.twitch.moonmoon.rpengine2.model.attribute;

import tv.twitch.moonmoon.rpengine2.model.Model;

import java.util.Optional;

/**
 * An attribute is a key-value pair that describes something about a
 * {@link tv.twitch.moonmoon.rpengine2.model.player.RpPlayer}. Values may be one of the
 * {@link AttributeType}s.
 */
public interface Attribute extends Model {

    /**
     * Returns this Attribute's unique name
     *
     * @return Attribute name
     */
    String getName();

    /**
     * Returns this Attributes display name
     *
     * @return Display name
     */
    String getDisplay();

    /**
     * Returns this attributes {@link AttributeType}
     *
     * @return Attribute type
     */
    AttributeType getType();

    /**
     * Returns the default value for this attribute or empty if none
     *
     * @return Default value
     */
    Optional<Object> getDefaultValue();

    /**
     * Returns the format string for this attribute or empty if none
     *
     * @return Format string
     */
    Optional<String> getFormatString();

    /**
     * Returns true if this is the identity attribute
     *
     * @return True if identity attribute
     */
    boolean isIdentity();

    /**
     * Returns true if this is the marker attribute
     *
     * @return true if marker attribute
     */
    boolean isMarker();

    /**
     * Returns true if this is the title attribute
     *
     * @return True if title attribute
     */
    boolean isTitle();
}
