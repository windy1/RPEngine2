package tv.twitch.moonmoon.rpengine2.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.cmd.admin.AdminCommand;
import tv.twitch.moonmoon.rpengine2.cmd.card.CardCommand;
import tv.twitch.moonmoon.rpengine2.cmd.card.CardSetCommand;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Commands {

    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> executors = new HashMap<>();

    @Inject
    public Commands(
        JavaPlugin plugin,
        AdminCommand adminCommand,
        CardCommand cardCommand,
        CardSetCommand cardSetCommand
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        executors.put("rpengine", adminCommand);
        executors.put("card", cardCommand);
        executors.put("cardset", cardSetCommand);
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
