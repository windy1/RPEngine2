package tv.twitch.moonmoon.rpengine2.data.select;

import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.attribute.impl.DefaultAttribute;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.model.select.impl.DefaultSelect;
import tv.twitch.moonmoon.rpengine2.task.Callback;

import java.util.Optional;
import java.util.Set;

public interface SelectRepo extends Repo {
    /**
     * Returns a set of all loaded selects.
     *
     * @return Set of loaded selects
     */
    Set<Select> getSelects();

    /**
     * Returns the {@link DefaultSelect} with the specified name, or empty if not found
     *
     * @param name Select name
     * @return Select
     */
    Optional<Select> getSelect(String name);

    /**
     * Creates a new select asynchronously
     *
     * @param name Select name
     * @param callback Callback invoked upon completion
     */
    void createSelectAsync(String name, Callback<Void> callback);

    /**
     * Creates a new select
     *
     * @param name Select name
     */
    void createSelect(String name);

    /**
     * Removes the specified select asynchronously. Note: this method will fail if the select is
     * currently added as an {@link DefaultAttribute}.
     *
     * @param name Select name
     * @param callback Callback to invoke upon completion
     */
    void removeSelectAsync(String name, Callback<Void> callback);

    /**
     * Removes the option with the specified select name and option name asynchronously. Note: this
     * method will fail if the select you are trying to remove the option from is currently added
     * as an {@link DefaultAttribute}.
     *
     * @param selectName Name of select
     * @param option Name of option
     * @param callback Callback to invoke upon completion
     */
    void removeOptionAsync(String selectName, String option, Callback<Void> callback);

    /**
     * Creates a new {@link tv.twitch.moonmoon.rpengine2.model.select.Option} asynchronously
     *
     * @param selectName Name of select to create option under
     * @param option Name of new option
     * @param display Display name
     * @param color Option color
     * @param callback Callback to invoke upon completion
     */
    void createOptionAsync(
        String selectName,
        String option,
        String display,
        String color,
        Callback<Void> callback
    );

    /**
     * Creates a new {@link tv.twitch.moonmoon.rpengine2.model.select.Option}
     *
     * @param selectName Name of select to create option under
     * @param option Name of new option
     * @param display Display name
     * @param color Option color
     */
    void createOption(String selectName, String option, String display, String color);
}
