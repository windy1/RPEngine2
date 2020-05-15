package tv.twitch.moonmoon.rpengine2.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.cmd.card.CardCommand;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Commands {

    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> executors = new HashMap<>();

    @Inject
    public Commands(JavaPlugin plugin, RpCommand rpCommand, CardCommand cardCommand) {
        this.plugin = Objects.requireNonNull(plugin);
        executors.put("rpengine", rpCommand);
        executors.put("card", cardCommand);
    }

    public void register() {
        ConfigurationSection commandConfig = plugin.getConfig()
            .getConfigurationSection("commands");

        if (commandConfig == null) {
            return;
        }

        for (String cmd : plugin.getDescription().getCommands().keySet()) {
            if (commandConfig.getBoolean(cmd)) {
                Objects.requireNonNull(plugin.getCommand(cmd))
                    .setExecutor(executors.get(cmd));
            }
        }
    }
}
