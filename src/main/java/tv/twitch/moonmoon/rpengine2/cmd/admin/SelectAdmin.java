package tv.twitch.moonmoon.rpengine2.cmd.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class SelectAdmin {

    private static final List<CommandUsage> USAGES = new ArrayList<>();
    private static final Help HELP = new Help(
        ChatColor.BLUE + "Select Sub Commands: ", USAGES
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
                return handleAdd(sender, StringUtils.splice(args, 1));
            case "addopt":
                return handleAddOpt(sender, StringUtils.splice(args, 1));
            case "remove":
                return handleRemove(sender, StringUtils.splice(args, 1));
            case "removeopt":
                return handleRemoveOpt(sender, StringUtils.splice(args, 1));
            default:
                return false;
        }
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        selectRepo.createSelectAsync(name, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                sender.sendMessage(ChatColor.RED + err.get());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Select created");
            }
        });

        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        selectRepo.removeSelectAsync(name, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                sender.sendMessage(ChatColor.RED + err.get());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Select removed");
            }
        });

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
            color = StringUtils.getChatColor(args[2]).orElse(null);
            if (color == null) {
                sender.sendMessage(ChatColor.RED + "Invalid chat color");
                return true;
            }
        }

        if (args.length > 3) {
            displayName = args[3];
        }

        selectRepo.createOptionAsync(selectName, name, displayName, color, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                sender.sendMessage(ChatColor.RED + err.get());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Option created");
            }
        });

        return true;
    }

    private boolean handleRemoveOpt(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String selectName = args[0];
        String name = args[1];

        selectRepo.removeOptionAsync(selectName, name, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                sender.sendMessage(ChatColor.RED + err.get());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Option removed");
            }
        });

        return true;
    }
}
