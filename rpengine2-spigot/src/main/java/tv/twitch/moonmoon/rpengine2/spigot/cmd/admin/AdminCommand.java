package tv.twitch.moonmoon.rpengine2.spigot.cmd.admin;

import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminCommand extends AbstractCoreCommandExecutor {

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
        super(plugin);

        this.attributeAdmin = Objects.requireNonNull(attributeAdmin);
        this.selectAdmin = Objects.requireNonNull(selectAdmin);
        this.dump = Objects.requireNonNull(dump);

        PluginDescriptionFile desc = plugin.getDescription();
        String version = desc.getVersion();
        String helpHeader = String.format(
            ChatColor.BLUE + "%s %s", PLUGIN_DISPLAY_NAME, version
        );

        help = new Help(helpHeader, USAGES);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return help.handle(sender, new String[0]);
        }

        switch(args[0]) {
            case "help":
                return help.handle(sender, args);
            case "attribute":
            case "at":
                return attributeAdmin.handle(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "select":
            case "sel":
                return selectAdmin.handle(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "dump":
                return dump.handle(sender, StringUtils.splice(args, 1));
            default:
                return false;
        }
    }

    @Override
    public String getConfigPath() {
        return "commands.rpengine";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }
}
