package tv.twitch.moonmoon.rpengine2.data.attribute;

import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.attribute.impl.DefaultAttribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.impl.DefaultRpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.util.Callback;

import java.util.Optional;
import java.util.Set;

/**
 * Manages the {@link DefaultAttribute} model
 */
public interface AttributeRepo extends Repo {

    /**
     * Returns a set of all loaded attributes
     *
     * @return Set of loaded attributes
     */
    Set<Attribute> getAttributes();

    /**
     * Returns the attribute with the specified name, or empty if not found
     *
     * @param name Attribute name
     * @return Attribute
     */
    Optional<Attribute> getAttribute(String name);

    /**
     * Returns the `identity` attribute if set, empty otherwise
     *
     * @return Identity attribute
     */
    Optional<Attribute> getIdentity();

    /**
     * Returns the `marker` attribute if set, empty otherwise
     *
     * @return Marker attribute
     */
    Optional<Attribute> getMarker();

    /**
     * Returns the `title` attribute if set, empty otherwise
     *
     * @return Title attribute
     */
    Optional<Attribute> getTitle();

    /**
     * Creates an attribute asynchronously
     *
     * @param name Attribute name
     * @param type Attribute type
     * @param display Display name
     * @param defaultValue Default value
     * @param callback Callback invoked upon completion
     */
    void createAttributeAsync(
        String name,
        AttributeType type,
        String display,
        String defaultValue,
        Callback<Void> callback
    );

    /**
     * Creates an attribute
     *
     * @param name Attribute name
     * @param type Attribute type
     * @param display Display name
     * @param defaultValue Default value
     */
    void createAttribute(String name, AttributeType type, String display, String defaultValue);

    /**
     * Removes an attribute asynchronously. Note: this also moves the corresponding
     * {@link DefaultRpPlayerAttribute} for all players
     *
     * @param name Attribute name
     * @param callback Callback invoked upon completion
     */
    void removeAttributeAsync(String name, Callback<Void> callback);

    /**
     * Sets an attributes default value asynchronously
     *
     * @param name Attribute name
     * @param defaultValue Default value
     * @param callback Callback invoked upon completion
     */
    void setDefaultAsync(String name, String defaultValue, Callback<Void> callback);

    /**
     * Sets an attributes display name asynchronously
     *
     * @param name Attribute name
     * @param display Display name
     * @param callback Callback invoked upon completion
     */
    void setDisplayAsync(String name, String display, Callback<Void> callback);

    /**
     * Sets an attributes format string asynchronously
     *
     * @param name Attribute name
     * @param formatString Format string
     * @param callback Callback invoked upon completion
     */
    void setFormatAsync(String name, String formatString, Callback<Void> callback);

    /**
     * Sets the specified attribute as the `identity` attribute asynchronously
     *
     * @see RpPlayerRepo#getIdentity(RpPlayer)
     * @param name Attribute name
     * @param callback Callback invoked upon completion
     */
    void setIdentityAsync(String name, Callback<Void> callback);

    /**
     * Sets the specified attribute as the `identity` attribute
     *
     * @see RpPlayerRepo#getIdentity(RpPlayer)
     * @param name Attribute name
     */
    void setIdentity(String name);

    /**
     * Clears the identity attribute asynchronously if set
     *
     * @param callback Callback invoked upon completion
     */
    void clearIdentityAsync(Callback<Void> callback);

    /**
     * Sets the specified attribute as the `marker` attribute asynchronously
     *
     * @param name Attribute name
     * @param callback Callback invoked upon completion
     */
    void setMarkerAsync(String name, Callback<Void> callback);

    /**
     * Sets the specified attribute as the `marker` attribute
     *
     * @param name Attribute name
     */
    void setMarker(String name);

    /**
     * Clears the marker attribute asynchronously if set
     *
     * @param callback Callback invoked upon completion
     */
    void clearMarkerAsync(Callback<Void> callback);

    /**
     * Sets the specified attribute as the `title` attribute asynchronously
     *
     * @see RpPlayerRepo#getTitle(RpPlayer)
     * @param name Attribute name
     * @param callback Callback invoked upon completion
     */
    void setTitleAsync(String name, Callback<Void> callback);

    /**
     * Sets the specified attribute as the `title` attribute
     *
     * @param name Attribute name
     */
    void setTitle(String name);

    /**
     * Clears the title attribute asynchronously if set
     *
     * @param callback Callback invoked upon completion
     */
    void clearTitleAsync(Callback<Void> callback);
}
