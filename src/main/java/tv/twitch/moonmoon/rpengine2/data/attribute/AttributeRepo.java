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
}
