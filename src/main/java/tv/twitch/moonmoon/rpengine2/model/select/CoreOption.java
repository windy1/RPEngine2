package tv.twitch.moonmoon.rpengine2.model.select;

import java.time.Instant;
import java.util.Objects;

/**
 * A single choice within a {@link Select}
 */
public class CoreOption implements Option {

    protected final int id;
    protected final int selectId;
    protected final Instant created;
    protected final String name;
    protected final String display;

    public CoreOption(
        int id,
        int selectId,
        Instant created,
        String name,
        String display
    ) {
        this.id = id;
        this.selectId = selectId;
        this.created = Objects.requireNonNull(created);
        this.name = Objects.requireNonNull(name);
        this.display = Objects.requireNonNull(display);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getSelectId() {
        return selectId;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoreOption option = (CoreOption) o;
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
            '}';
    }
}
