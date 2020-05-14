package tv.twitch.moonmoon.rpengine2.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum AttributeType {
    String("string"),
    Number("number"),
    Group("group");

    private static final Map<String, AttributeType> idMap = new HashMap<>();

    static {
        for (AttributeType a : AttributeType.values()) {
            idMap.put(a.id, a);
        }
    }

    private final String id;

    AttributeType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static Optional<AttributeType> findById(String id) {
        return Optional.ofNullable(idMap.get(id));
    }
}
