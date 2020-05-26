package tv.twitch.moonmoon.rpengine2.spigot.data.select;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.spigot.model.select.SpigotOption;
import tv.twitch.moonmoon.rpengine2.util.Callback;

/**
 * Manages the {@link Select} and {@link SpigotOption} models.
 * A user-defined select allows player to choose a value for an attribute based on a pre-determined
 * set of choices.
 */
public interface SpigotSelectRepo extends SelectRepo {

    /**
     * Creates a new {@link SpigotOption} asynchronously
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
        ChatColor color,
        Callback<Void> callback
    );

    /**
     * Creates a new {@link SpigotOption}
     *
     * @param selectName Name of select to create option under
     * @param option Name of new option
     * @param display Display name
     * @param color Option color
     */
    void createOption(String selectName, String option, String display, ChatColor color);
}
