package tv.twitch.moonmoon.rpengine2.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.RpAttributeRepo;
import tv.twitch.moonmoon.rpengine2.model.AttributeType;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AttributeAdmin {

    private static final List<CommandUsage> USAGES = new ArrayList<>();

    private static final Help HELP = new Help(
        ChatColor.BLUE + "Attribute Sub Commands: ", USAGES
    );

    static {
        List<ArgumentLabel> addArgs = new ArrayList<>();
        addArgs.add(new ArgumentLabel("name", true));
        addArgs.add(new ArgumentLabel("display_name", false));
        addArgs.add(new ArgumentLabel("default_value", false));
        USAGES.add(new CommandUsage("add", addArgs));

    }

    private final RpAttributeRepo attributeRepo;

    @Inject
    public AttributeAdmin(RpAttributeRepo attributeRepo) {
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
            case "set":
            default:
                return false;
        }
    }

    public boolean handleAdd(CommandSender sender, String[] args) {
        if (args.length == 0) {
            HELP.handle(sender, new String[] { "help" });
            return true;
        }

        String name = args[0];
        AttributeType type = AttributeType.String;
        String displayName = name;
        String defaultValue = null;

        if (args.length > 1) {
            displayName = args[1];
        }

        if (args.length > 2) {
            defaultValue = args[2];
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
}
