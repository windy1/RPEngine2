package tv.twitch.moonmoon.rpengine2.model.player;

import tv.twitch.moonmoon.rpengine2.model.Model;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.attribute.impl.DefaultAttribute;

import java.util.Optional;

/**
 * An {@link DefaultAttribute} that belongs to an
 * {@link RpPlayer}
 */
public interface RpPlayerAttribute extends Model {

    /**
     * Returns the unique ID for the linked
     * {@link DefaultAttribute}
     *
     * @return Attribute ID
     */
    int getAttributeId();

    /**
     * Returns the name of the attribute
     *
     * @return Attribute name
     */
    String getName();

    /**
     * Returns the value the attribute is set to for the player
     *
     * @return Attribute value for player
     */
    Optional<Object> getValue();

    /**
     * Returns the type of the attribute
     *
     * @return Attribute type
     */
    AttributeType getType();
}
