package tv.twitch.moonmoon.rpengine2.spigot.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.logging.Logger;

public interface ModuleLoader {

    Logger getLogger();

    Plugin getPlugin();

    default <T> boolean requireOk(Result<T> r) {
        Optional<String> err = r.getError();
        if (err.isPresent()) {
            getLogger().warning(err.get());
            Bukkit.getPluginManager().disablePlugin(getPlugin());
            return false;
        }
        return true;
    }
}
