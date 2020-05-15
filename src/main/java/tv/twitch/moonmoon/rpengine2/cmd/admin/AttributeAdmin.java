package tv.twitch.moonmoon.rpengine2.cmd.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.model.AttributeType;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class AttributeAdmin {

    private static final List<CommandUsage> USAGES = new ArrayList<>();

    private static final Help HELP = new Help(
        ChatColor.BLUE + "Attribute Sub Commands: ", USAGES
    );

    static {
        USAGES.add(new CommandUsage("add", Arrays.asList(
            new ArgumentLabel("name", true),
            new ArgumentLabel("default_value", false),
            new ArgumentLabel("display_name", false)
        )));

        USAGES.add(new CommandUsage("remove", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));
    }

    private final AttributeRepo attributeRepo;

    @Inject
    public AttributeAdmin(AttributeRepo attributeRepo) {
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            HELP.handle(sender, new String[] { "help" });
            return true;
        }

        String[] splicedArgs = StringUtils.splice(args, 1);

        switch (args[0]) {
            case "add":
                return handleAdd(sender, splicedArgs);
            case "remove":
                return handleRemove(sender, splicedArgs);
            case "set":
            default:
                return false;
        }
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];
        String defaultValue = null;
        String displayName = name;
        AttributeType type = AttributeType.String;

        if (args.length > 1) {
            defaultValue = args[1];
        }

        if (args.length > 2) {
            displayName = args[2];
        }

        attributeRepo.createAttributeAsync(name, type, displayName, defaultValue, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                sender.sendMessage(ChatColor.RED + err.get());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Attribute added");
            }
        });

        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        attributeRepo.removeAttributeAsync(name, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                sender.sendMessage(ChatColor.RED + err.get());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Attribute removed");
            }
        });

        return true;
    }
}
