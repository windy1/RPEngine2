package tv.twitch.moonmoon.rpengine2.model.select;

import org.bukkit.ChatColor;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class Option {

    private final int id;
    private final int selectId;
    private final Instant created;
    private final String name;
    private final String display;
    private final ChatColor color;

    public Option(
        int id,
        int selectId,
        Instant created,
        String name,
        String display,
        ChatColor color
    ) {
        this.id = id;
        this.selectId = selectId;
        this.created = Objects.requireNonNull(created);
        this.name = Objects.requireNonNull(name);
        this.display = Objects.requireNonNull(display);
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public int getSelectId() {
        return selectId;
    }

    public Instant getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public Optional<ChatColor> getColor() {
        return Optional.ofNullable(color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option option = (Option) o;
        return id == option.id &&
            selectId == option.selectId &&
            Objects.equals(created, option.created) &&
            Objects.equals(name, option.name) &&
            Objects.equals(display, option.display) &&
            color == option.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Option{" +
            "id=" + id +
            ", selectId=" + selectId +
            ", created=" + created +
            ", name='" + name + '\'' +
            ", display='" + display + '\'' +
            ", color=" + (color != null ? color.name() : "") +
            '}';
    }
}
