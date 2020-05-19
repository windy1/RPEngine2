package tv.twitch.moonmoon.rpengine2.model.select;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.model.Model;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A single choice within a {@link Select}
 */
public class Option implements Model {

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

    @Override
    public int getId() {
        return id;
    }

    /**
     * Returns this option's {@link Select} parent ID
     *
     * @return Parent ID
     */
    public int getSelectId() {
        return selectId;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    /**
     * Returns this options name
     *
     * @return Option name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this options display name
     *
     * @return Option display name
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Returns this options color
     *
     * @return Option color
     */
    public Optional<ChatColor> getColor() {
        return Optional.ofNullable(color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option option = (Option) o;
        return Objects.equals(name, option.name);
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
