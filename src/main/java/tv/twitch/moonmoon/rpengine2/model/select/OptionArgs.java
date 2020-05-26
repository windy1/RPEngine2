package tv.twitch.moonmoon.rpengine2.model.select;

import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Objects;
import java.util.Optional;

public class OptionArgs {

    private final String selectName;
    private final String option;
    private final String display;

    private final Select select;
    private final int selectId;

    public OptionArgs(
        String selectName,
        String option,
        String display,
        Select select,
        int selectId
    ) {
        this.selectName = Objects.requireNonNull(selectName);
        this.option = Objects.requireNonNull(option);
        this.display = Objects.requireNonNull(display);
        this.select = select;
        this.selectId = selectId;
    }

    public OptionArgs(
        String selectName,
        String option,
        String display,
        Select select
    ) {
        this(selectName, option, display, select, 0);
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

        if (select.getOption(option).isPresent()) {
            return Result.error("Option already exists");
        }

        return Result.ok(new OptionArgs(
           selectName,
           option,
           display,
           select,
           select.getId()
        ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionArgs that = (OptionArgs) o;
        return Objects.equals(selectName, that.selectName) &&
            Objects.equals(option, that.option);
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
            ", select=" + select +
            ", selectId=" + selectId +
            '}';
    }
}
