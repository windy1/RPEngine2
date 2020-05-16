package tv.twitch.moonmoon.rpengine2.model.attribute;

import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.IllegalFormatException;
import java.util.Objects;
import java.util.Optional;

public class AttributeArgs {

    private final String name;
    private final AttributeType type;
    private final String defaultValue;
    private final String formatString;

    private final SelectRepo selectRepo;
    private final AttributeRepo attributeRepo;

    public AttributeArgs(
        String name,
        AttributeType type,
        String defaultValue,
        String formatString,
        SelectRepo selectRepo,
        AttributeRepo attributeRepo
    ) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.defaultValue = defaultValue;
        this.formatString = formatString;
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
    }

    public String getName() {
        return name;
    }

    public AttributeType getType() {
        return type;
    }

    public Optional<String> getFormatString() {
        return Optional.ofNullable(formatString);
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public Result<AttributeArgs> canCreate() {
        if (attributeRepo.getAttribute(name).isPresent()) {
            return Result.error("Attribute already exists");
        }

        return canUpdate();
    }

    public Result<AttributeArgs> canUpdate() {
        String newDefault = defaultValue;

        if (type == AttributeType.Select) {
            Optional<Select> s = selectRepo.getSelect(name);
            if (!s.isPresent()) {
                return Result.error("Select not found (/rpengine select)");
            }

            Optional<Option> o = s.get().getOption(defaultValue);
            if (!o.isPresent()) {
                return Result.error("Unknown option (/rpengine select)");
            }

            newDefault = Integer.toString(o.get().getId());
        }

        Object parsedDefault = null;

        if (newDefault != null) {
            Result<Object> d = type.parse(newDefault);

            Optional<String> parseErr = d.getError();
            if (parseErr.isPresent()) {
                return Result.error(parseErr.get());
            }

            parsedDefault = d.get();
        }

        if (formatString != null) {
            if (parsedDefault == null) {
                String message =
                    "Cannot set a format string on an attribute with no default value";
                return Result.error(message);
            }

            try {
                //noinspection ResultOfMethodCallIgnored
                String.format(formatString, parsedDefault);
            } catch (IllegalFormatException e) {
                String message = String.format("Invalid format string: `%s`", e.getMessage());
                return Result.error(message);
            }
        }

        return Result.ok(new AttributeArgs(
            name,
            type,
            newDefault,
            formatString, selectRepo,
            attributeRepo
        ));
    }
}
