package tv.twitch.moonmoon.rpengine2.data.attribute;

import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.AttributeType;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface AttributeRepo extends Repo {

    Set<Attribute> getAttributes();

    Optional<Attribute> getAttribute(String name);

    void createAttributeAsync(
        String name,
        AttributeType type,
        String display,
        String defaultValue,
        Consumer<Result<Void>> callback
    );

    void load();
}
