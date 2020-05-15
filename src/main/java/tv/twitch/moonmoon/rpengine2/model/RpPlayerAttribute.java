package tv.twitch.moonmoon.rpengine2.model;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class RpPlayerAttribute {

    private final int id;
    private final Instant created;
    private final String display;
    private final String name;
    private final Object value;

    public RpPlayerAttribute(int id, Instant created, String display, String name, Object value) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.display = Objects.requireNonNull(display);
        this.name = Objects.requireNonNull(name);
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Instant getCreated() {
        return created;
    }

    public String getDisplay() {
        return display;
    }

    public String getName() {
        return name;
    }

    public Optional<Object> getValue() {
        return Optional.ofNullable(value);
    }

    public String getStringValue() {
        return getValue().map(Object::toString).orElse("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpPlayerAttribute that = (RpPlayerAttribute) o;
        return id == that.id &&
            Objects.equals(created, that.created) &&
            Objects.equals(display, that.display) &&
            Objects.equals(name, that.name) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "RpPlayerAttribute{" +
            "id=" + id +
            ", created=" + created +
            ", display='" + display + '\'' +
            ", name='" + name + '\'' +
            ", value=" + value +
            '}';
    }
}
