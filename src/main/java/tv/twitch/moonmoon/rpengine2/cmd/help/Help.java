package tv.twitch.moonmoon.rpengine2.cmd.help;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

public class Help {

    private static final String BASE_HELP =
        ChatColor.BLUE + "Use " + ChatColor.GREEN + "/rpengine help" + ChatColor.BLUE
            + " to view available admin commands.";

    private final String header;
    private final List<CommandUsage> usages;

    public Help(String header, List<CommandUsage> usages) {
        this.header = Objects.requireNonNull(header);
        this.usages = Objects.requireNonNull(usages);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(header);
            sender.sendMessage(BASE_HELP);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            return showHelp(sender);
        }

        return false;
    }

    public boolean showHelp(CommandSender sender) {
        sender.sendMessage(header);
        for (CommandUsage usage : usages) {
            sender.sendMessage(usage.toString());
        }
        return true;
    }
}
