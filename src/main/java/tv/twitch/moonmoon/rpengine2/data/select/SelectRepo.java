package tv.twitch.moonmoon.rpengine2.data.select;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.Callback;

import java.util.Optional;
import java.util.Set;

public interface SelectRepo extends Repo {

    Set<Select> getSelects();

    Optional<Select> getSelect(String name);

    void createSelectAsync(String name, Callback<Void> callback);

    void createSelect(String name);

    void removeSelectAsync(String name, Callback<Void> callback);

    void createOptionAsync(
        String selectName,
        String option,
        String display,
        ChatColor color,
        Callback<Void> callback
    );

    void createOption(String selectName, String option, String display, ChatColor color);

    void removeOptionAsync(String selectName, String option, Callback<Void> callback);
}
