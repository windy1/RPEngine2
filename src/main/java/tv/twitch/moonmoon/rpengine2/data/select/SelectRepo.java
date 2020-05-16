package tv.twitch.moonmoon.rpengine2.data.select;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface SelectRepo extends Repo {

    Set<Select> getSelects();

    Optional<Select> getSelect(String name);

    void createSelectAsync(String name, Consumer<Result<Void>> callback);

    void createSelect(String name);

    void createOptionAsync(
        String selectName,
        String option,
        String display,
        ChatColor color,
        Consumer<Result<Void>> callback
    );

    void createOption(String selectName, String option, String display, ChatColor color);
}
