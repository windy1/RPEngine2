package tv.twitch.moonmoon.rpengine2.model.attribute;

import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Objects;
import java.util.Optional;

public class AttributeArgs {

    private final String name;
    private final AttributeType type;
    private final String display;
    private final String defaultValue;

    private final SelectRepo selectRepo;

    public AttributeArgs(
        String name,
        AttributeType type,
        String display,
        String defaultValue,
        SelectRepo selectRepo
    ) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.display = Objects.requireNonNull(display);
        this.defaultValue = defaultValue;
        this.selectRepo = Objects.requireNonNull(selectRepo);
    }

    public String getName() {
        return name;
    }

    public AttributeType getType() {
        return type;
    }

    public String getDisplay() {
        return display;
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public Result<AttributeArgs> clean() {
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

        if (newDefault != null) {
            Optional<String> parseErr = type.parse(newDefault).getError();
            if (parseErr.isPresent()) {
                return Result.error(parseErr.get());
            }
        }

        return Result.ok(new AttributeArgs(
            name,
            type,
            display,
            newDefault,
            selectRepo
        ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeArgs that = (AttributeArgs) o;
        return Objects.equals(name, that.name) &&
            type == that.type &&
            Objects.equals(display, that.display) &&
            Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "AttributeArgs{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", display='" + display + '\'' +
            ", defaultValue='" + defaultValue + '\'' +
            '}';
    }
}
