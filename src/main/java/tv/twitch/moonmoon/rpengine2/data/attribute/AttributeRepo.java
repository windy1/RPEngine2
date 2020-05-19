package tv.twitch.moonmoon.rpengine2.data.attribute;

import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.util.Callback;

import java.util.Optional;
import java.util.Set;

public interface AttributeRepo extends Repo {

    Set<Attribute> getAttributes();

    Optional<Attribute> getAttribute(String name);

    Optional<Attribute> getIdentity();

    Optional<Attribute> getMarker();

    Optional<Attribute> getTitle();

    void createAttributeAsync(
        String name,
        AttributeType type,
        String display,
        String defaultValue,
        Callback<Void> callback
    );

    void createAttribute(String name, AttributeType type, String display, String defaultValue);

    void removeAttributeAsync(String name, Callback<Void> callback);

    void setDefaultAsync(String name, String defaultValue, Callback<Void> callback);

    void setDisplayAsync(String name, String display, Callback<Void> callback);

    void setFormatAsync(String name, String formatString, Callback<Void> callback);

    void setIdentityAsync(String name, Callback<Void> callback);

    void setIdentity(String name);

    void clearIdentityAsync(Callback<Void> callback);

    void setMarkerAsync(String name, Callback<Void> callback);

    void setMarker(String name);

    void clearMarkerAsync(Callback<Void> callback);

    void setTitleAsync(String name, Callback<Void> callback);

    void setTitle(String name);

    void clearTitleAsync(Callback<Void> callback);
}
