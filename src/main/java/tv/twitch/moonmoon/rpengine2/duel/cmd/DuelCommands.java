package tv.twitch.moonmoon.rpengine2.duel.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.cmd.Commands;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DuelCommands implements Commands {

    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> executors = new HashMap<>();

    @Inject
    public DuelCommands(
        JavaPlugin plugin,
        DuelRulesCommand duelRulesCommand,
        DuelCommand duelCommand
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        executors.put("duelrules", duelRulesCommand);
        executors.put("duel", duelCommand);
    }

    @Override
    public Map<String, CommandExecutor> getExecutors() {
        return executors;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getConfigPath() {
        return "duels.commands";
    }
}
