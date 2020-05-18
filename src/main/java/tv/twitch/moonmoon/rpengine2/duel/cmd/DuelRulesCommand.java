package tv.twitch.moonmoon.rpengine2.duel.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;

import javax.inject.Inject;

public class DuelRulesCommand extends AbstractCoreCommandExecutor {

    private static final String HEADER = ChatColor.BLUE + "Duel rules:";

    private final String[] rules = new String[] {
        "1. You must be within %d blocks of the player you would like to duel with",
        "2. The player you would like to duel must accept by also running /duel <you>",
        "3. The first person's health to reach zero loses",
        "4. Upon loss, you will not die, and you will not lose the items in your inventory",
        "5. Once over, both players will be teleported back to the location where you started " +
            "the duel",
        "6. During the duel, other players will not be able to damage you, however your " +
            "environment can",
        "7. If neither player is victorious within %d seconds, the duel will be cancelled and " +
            "declared a tie"
    };

    private static final String HELP = ChatColor.BLUE + "To start a duel, run /duel <player>";

    @Inject
    public DuelRulesCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        FileConfiguration config = plugin.getConfig();
        int startRange = config.getInt("duels.startRange", 10);
        int maxSecs = config.getInt("duels.maxSecs", 300);

        rules[0] = String.format(rules[0], startRange);
        rules[6] = String.format(rules[6], maxSecs);

        sender.sendMessage(HEADER);

        for (String rule : rules) {
            sender.sendMessage(ChatColor.BLUE + "# " + ChatColor.WHITE + rule);
        }

        sender.sendMessage(HELP);

        return true;
    }

    @Override
    public String getConfigPath() {
        return "duel.commands.duelrules";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }
}
