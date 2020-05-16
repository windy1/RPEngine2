package tv.twitch.moonmoon.rpengine2.model.select;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Objects;
import java.util.Optional;

public class OptionArgs {

    private final String selectName;
    private final String option;
    private final String display;
    private final ChatColor color;

    private final Select select;
    private final int selectId;

    public OptionArgs(
        String selectName,
        String option,
        String display,
        ChatColor color,
        Select select,
        int selectId
    ) {
        this.selectName = Objects.requireNonNull(selectName);
        this.option = Objects.requireNonNull(option);
        this.display = Objects.requireNonNull(display);
        this.color = color;
        this.select = select;
        this.selectId = selectId;
    }

    public OptionArgs(
        String selectName,
        String option,
        String display,
        ChatColor color,
        Select select
    ) {
        this(selectName, option, display, color, select, 0);
    }

    public String getSelectName() {
        return selectName;
    }

    public String getOption() {
        return option;
    }

    public String getDisplay() {
        return display;
    }

    public Optional<ChatColor> getColor() {
        return Optional.ofNullable(color);
    }

    public Optional<Select> getSelect() {
        return Optional.ofNullable(select);
    }

    public int getSelectId() {
        return selectId;
    }

    public Result<OptionArgs> clean() {
        if (select == null) {
            return Result.error("Select not found");
        }

        return Result.ok(new OptionArgs(
           selectName,
           option,
           display,
           color,
           select,
           select.getId()
        ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionArgs that = (OptionArgs) o;
        return selectId == that.selectId &&
            Objects.equals(selectName, that.selectName) &&
            Objects.equals(option, that.option) &&
            Objects.equals(display, that.display) &&
            color == that.color &&
            Objects.equals(select, that.select);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectName, option);
    }

    @Override
    public String toString() {
        return "OptionArgs{" +
            "selectName='" + selectName + '\'' +
            ", option='" + option + '\'' +
            ", display='" + display + '\'' +
            ", color=" + color +
            ", select=" + select +
            ", selectId=" + selectId +
            '}';
    }
}
