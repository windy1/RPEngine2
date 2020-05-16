package tv.twitch.moonmoon.rpengine2.model.attribute;

import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public enum AttributeType {
    String("string"),
    Number("number"),
    Select("select");

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

    public Result<Object> parse(String value) {
        Objects.requireNonNull(value);
        switch (this) {
            case String:
                return Result.ok(value);
            case Number: {
                try {
                    return Result.ok(Float.parseFloat(value));
                } catch (NumberFormatException e) {
                    return Result.error("Invalid number value");
                }
            }
            case Select:
                try {
                    return Result.ok(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    return Result.error("Invalid select value");
                }
            default:
                return Result.error("unknown type");
        }
    }

    public static Optional<AttributeType> findById(String id) {
        return Optional.ofNullable(idMap.get(id));
    }
}
