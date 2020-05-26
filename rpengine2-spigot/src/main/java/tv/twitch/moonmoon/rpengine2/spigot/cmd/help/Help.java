package tv.twitch.moonmoon.rpengine2.spigot.cmd.help;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

public class Help {

    private static final String BASE_HELP =
        ChatColor.BLUE + "Use " + ChatColor.GREEN + "/rpengine help" + ChatColor.BLUE
            + " to view available admin commands.";

    private final String header;
    private final String subheader;
    private final List<CommandUsage> usages;

    public Help(String header, String subheader, List<CommandUsage> usages) {
        this.header = Objects.requireNonNull(header);
        this.subheader = subheader;
        this.usages = Objects.requireNonNull(usages);
    }

    public Help(String header, List<CommandUsage> usages) {
        this(header, null, usages);
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
        sender.sendMessage(ChatColor.BLUE + header);

        if (subheader != null) {
            sender.sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + subheader);
        }

        for (CommandUsage usage : usages) {
            sender.sendMessage(usage.toString());
        }

        return true;
    }
}
