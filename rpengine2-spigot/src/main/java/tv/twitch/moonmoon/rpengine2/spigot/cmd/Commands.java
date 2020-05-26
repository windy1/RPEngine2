package tv.twitch.moonmoon.rpengine2.spigot.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Map;
import java.util.Objects;

public interface Commands {

    Map<String, CommandExecutor> getExecutors();

    JavaPlugin getPlugin();

    String getConfigPath();

    default void register() {
        JavaPlugin plugin = getPlugin();
        Map<String, CommandExecutor> executors = getExecutors();

        ConfigurationSection commandConfig = plugin.getConfig()
            .getConfigurationSection(getConfigPath());

        if (commandConfig == null) {
            return;
        }

        for (String cmd : plugin.getDescription().getCommands().keySet()) {
            ConfigurationSection c = commandConfig.getConfigurationSection(cmd);
            if (c == null) {
                continue;
            }

            if (c.getBoolean("enabled")) {
                Objects.requireNonNull(plugin.getCommand(cmd))
                    .setExecutor(executors.get(cmd));
            }
        }
    }

    static <T> String mapResult(Result<T> r, String success) {
        return r.getError()
            .map(e -> ChatColor.RED + e)
            .orElseGet(() -> ChatColor.GREEN + success);
    }
}
