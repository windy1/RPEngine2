package tv.twitch.moonmoon.rpengine2.spigot.cmd.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.Commands;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.spigot.util.SpigotUtils;

import javax.inject.Inject;
import java.util.*;

public class SelectAdmin {

    private static final List<CommandUsage> USAGES = new ArrayList<>();
    private static final Help HELP = new Help(
        "Select Sub Commands: ",
        "Selects allow you to create attributes that must be one of the pre-determined values",
        USAGES
    );

    static {
        USAGES.add(new CommandUsage("add", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("addopt", Arrays.asList(
            new ArgumentLabel("select_name", true),
            new ArgumentLabel("name", true),
            new ArgumentLabel("color", false),
            new ArgumentLabel("display_name", false)
        )));

        USAGES.add(new CommandUsage("remove", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("removeopt", Arrays.asList(
            new ArgumentLabel("select_name", true),
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("list"));
    }

    private final SelectRepo selectRepo;

    @Inject
    public SelectAdmin(SelectRepo selectRepo) {
        this.selectRepo = Objects.requireNonNull(selectRepo);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            HELP.handle(sender, new String[] { "help" });
            return true;
        }

        switch (args[0]) {
            case "add":
                return handleAdd(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "addopt":
                return handleAddOpt(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "remove":
            case "rm":
                return handleRemove(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "removeopt":
            case "rmopt":
                return handleRemoveOpt(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "list":
                return handleList(sender);
            default:
                return false;
        }
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        selectRepo.createSelectAsync(name, r ->
            sender.sendMessage(Commands.mapResult(r, "Select created"))
        );

        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        selectRepo.removeSelectAsync(name, r ->
            sender.sendMessage(Commands.mapResult(r, "Select removed"))
        );

        return true;
    }

    private boolean handleAddOpt(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String selectName = args[0];
        String name = args[1];
        ChatColor color = null;
        String displayName = name;

        if (args.length > 2) {
            color = SpigotUtils.getChatColor(args[2]).orElse(null);
            if (color == null) {
                sender.sendMessage(ChatColor.RED + "Invalid chat color");
                return true;
            }
        }

        if (args.length > 3) {
            displayName = args[3];
        }

        String colorName = color != null ? color.name() : null;

        selectRepo.createOptionAsync(selectName, name, displayName, colorName, r ->
            sender.sendMessage(Commands.mapResult(r, "Option created"))
        );

        return true;
    }

    private boolean handleRemoveOpt(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String selectName = args[0];
        String name = args[1];

        selectRepo.removeOptionAsync(selectName, name, r ->
            sender.sendMessage(Commands.mapResult(r, "Option removed"))
        );

        return true;
    }

    private boolean handleList(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "Selects:");
        selectRepo.getSelects().stream()
            .map(SelectLabel::from)
            .forEach(s -> s.sendTo(sender));
        return true;
    }
}
