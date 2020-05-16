package tv.twitch.moonmoon.rpengine2.cmd.admin;

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
        USAGES.add(new CommandUsage("/rpengine select"));
        USAGES.add(new CommandUsage("/rpengine dump"));
    }

    private final AttributeAdmin attributeAdmin;
    private final SelectAdmin selectAdmin;
    private final Dump dump;
    private final Help help;

    @Inject
    public AdminCommand(
        Plugin plugin,
        AttributeAdmin attributeAdmin,
        SelectAdmin selectAdmin,
        Dump dump
    ) {
        this.attributeAdmin = Objects.requireNonNull(attributeAdmin);
        this.selectAdmin = Objects.requireNonNull(selectAdmin);
        this.dump = Objects.requireNonNull(dump);

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

        switch(args[0]) {
            case "help":
                return help.handle(sender, args);
            case "attribute":
                return attributeAdmin.handle(sender, StringUtils.splice(args, 1));
            case "select":
                return selectAdmin.handle(sender, StringUtils.splice(args, 1));
            case "dump":
                return dump.handle(sender, StringUtils.splice(args, 1));
            default:
                return false;
        }
    }
}