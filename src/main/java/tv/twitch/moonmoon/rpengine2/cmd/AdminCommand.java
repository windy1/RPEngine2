package tv.twitch.moonmoon.rpengine2.cmd;

import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminCommand implements CommandExecutor {

    private static final String PLUGIN_DISPLAY_NAME = "RPEngine";
    private static final List<CommandUsage> USAGES = new ArrayList<>();

    static {
        USAGES.add(new CommandUsage("/rpengine attribute"));
        USAGES.add(new CommandUsage("/card"));
    }

    private final AttributeAdmin attributeAdmin;
    private final Help help;

    @Inject
    public AdminCommand(Plugin plugin, AttributeAdmin attributeAdmin) {
        this.attributeAdmin = Objects.requireNonNull(attributeAdmin);

        PluginDescriptionFile desc = plugin.getDescription();
        String version = desc.getVersion();
        String helpHeader = String.format(
            "%s%s %s", ChatColor.BLUE, PLUGIN_DISPLAY_NAME, version
        );

        help = new Help(helpHeader, USAGES);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return help.handle(sender, new String[0]);
        }

        if (args[0].equalsIgnoreCase("help")) {
            return help.handle(sender, args);
        } else if (args[0].equalsIgnoreCase("attribute")) {
            return attributeAdmin.handle(sender, StringUtils.splice(args, 1));
        }

        return false;
    }
}
