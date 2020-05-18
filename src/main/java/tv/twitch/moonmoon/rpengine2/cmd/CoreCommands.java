package tv.twitch.moonmoon.rpengine2.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.cmd.action.PlayerActionMenuCommand;
import tv.twitch.moonmoon.rpengine2.cmd.admin.AdminCommand;
import tv.twitch.moonmoon.rpengine2.cmd.card.CardCommand;
import tv.twitch.moonmoon.rpengine2.cmd.card.CardSelectCommand;
import tv.twitch.moonmoon.rpengine2.cmd.card.CardSetCommand;
import tv.twitch.moonmoon.rpengine2.cmd.card.InspectCommand;
import tv.twitch.moonmoon.rpengine2.cmd.help.ColorListCommand;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CoreCommands implements Commands {

    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> executors = new HashMap<>();

    @Inject
    public CoreCommands(
        JavaPlugin plugin,
        AdminCommand adminCommand,
        CardCommand cardCommand,
        CardSetCommand cardSetCommand,
        CardSelectCommand cardSelectCommand,
        ColorListCommand colorListCommand,
        InspectCommand inspectCommand,
        PlayerActionMenuCommand playerActionMenuCommand
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        executors.put("rpengine", adminCommand);
        executors.put("card", cardCommand);
        executors.put("cardset", cardSetCommand);
        executors.put("cardselect", cardSelectCommand);
        executors.put("colorlist", colorListCommand);
        executors.put("inspect", inspectCommand);
        executors.put("playeractionmenu", playerActionMenuCommand);
    }

    @Override
    public Map<String, CommandExecutor> getExecutors() {
        return Collections.unmodifiableMap(executors);
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getConfigPath() {
        return "commands";
    }
}
