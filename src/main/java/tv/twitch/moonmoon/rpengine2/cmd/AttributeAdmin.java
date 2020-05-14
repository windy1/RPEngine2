package tv.twitch.moonmoon.rpengine2.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.data.RpAttributeRepo;
import tv.twitch.moonmoon.rpengine2.model.AttributeType;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class AttributeAdmin {

    private static final String USAGE = "/rpengine attribute [ add | remove | set ]";

    private static final String ADD_USAGE =
        "/rpengine attribute add " +
            "{ name } " +
            "[ type(string|number|group) ] " +
            "[ display_name ] " +
            "[ default_value ]";

    private final RpAttributeRepo attributeRepo;

    @Inject
    public AttributeAdmin(RpAttributeRepo attributeRepo) {
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(USAGE);
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
            sender.sendMessage(ADD_USAGE);
            return true;
        }

        String name = args[0];
        AttributeType type = AttributeType.String;
        String displayName = name;
        String defaultValue = null;

        if (args.length > 1) {
            type = AttributeType.findById(args[2]).orElse(null);
            if (type == null) {
                sender.sendMessage(ChatColor.RED + "Invalid attribute type");
                return false;
            }
        }

        if (args.length > 2) {
            displayName = args[3];
        }

        if (args.length > 3) {
            defaultValue = args[4];
        }

        attributeRepo.createAttributeAsync(name, type, displayName, defaultValue, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                sender.sendMessage(ChatColor.RED + err.get());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Attribute added");
            }
        });

        return false;
    }
}
