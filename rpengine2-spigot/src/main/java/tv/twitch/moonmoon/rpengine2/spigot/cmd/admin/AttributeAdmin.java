package tv.twitch.moonmoon.rpengine2.spigot.cmd.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.Commands;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class AttributeAdmin {

    private static final List<CommandUsage> USAGES = new ArrayList<>();
    private static final Help HELP = new Help(
        "Attribute Sub Commands: ",
        "Attributes are the key-value pairs that appear on player cards (/card)",
        USAGES
    );

    static {
        List<ArgumentLabel> addArgs = Arrays.asList(
            new ArgumentLabel("name", true),
            new ArgumentLabel("default_value", false),
            new ArgumentLabel("display_name", false)
        );

        USAGES.add(new CommandUsage("add", addArgs));
        USAGES.add(new CommandUsage("addnum", addArgs));

        USAGES.add(new CommandUsage("addselect", Arrays.asList(
            new ArgumentLabel("name", true),
            new ArgumentLabel("default_value", true),
            new ArgumentLabel("display_name", false)
        )));

        USAGES.add(new CommandUsage("remove", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("setdefault", Arrays.asList(
            new ArgumentLabel("name", true),
            new ArgumentLabel("default_value", true)
        )));

        USAGES.add(new CommandUsage("setdisplay", Arrays.asList(
            new ArgumentLabel("name", true),
            new ArgumentLabel("display_name", true)
        )));

        USAGES.add(new CommandUsage("setformat", Arrays.asList(
            new ArgumentLabel("name", true),
            new ArgumentLabel("format_string", true)
        )));

        USAGES.add(new CommandUsage("setidentity", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("clearidentity"));

        USAGES.add(new CommandUsage("setmarker", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("clearmarker"));

        USAGES.add(new CommandUsage("settitle", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("cleartitle"));
    }

    private final AttributeRepo attributeRepo;
    private final SelectRepo selectRepo;

    @Inject
    public AttributeAdmin(AttributeRepo attributeRepo, SelectRepo selectRepo) {
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            HELP.handle(sender, new String[] { "help" });
            return true;
        }

        String[] splicedArgs = tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1);

        switch (args[0]) {
            case "add":
                return handleAdd(AttributeType.String, sender, splicedArgs);
            case "addnum":
                return handleAdd(AttributeType.Number, sender, splicedArgs);
            case "addselect":
            case "addsel":
                return handleAdd(AttributeType.Select, sender, splicedArgs);
            case "remove":
            case "rm":
                return handleRemove(sender, splicedArgs);
            case "setdefault":
            case "setdef":
                return handleSetDefault(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "setdisplay":
                return handleSetDisplay(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "setformat":
            case "setfmt":
                return handleSetFormat(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "setidentity":
            case "setident":
                return handleSetIdentity(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "clearidentity":
            case "clearident":
                return handleClearIdentity(sender);
            case "setmarker":
            case "setmark":
                return handleSetMarker(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "clearmarker":
            case "clearmark":
                return handleClearMarker(sender);
            case "settitle":
                return handleSetTitle(sender, tv.twitch.moonmoon.rpengine2.util.StringUtils.splice(args, 1));
            case "cleartitle":
                return handleClearTitle(sender);
            default:
                return false;
        }
    }

    private boolean handleAdd(AttributeType type, CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (args.length < 2 && type == AttributeType.Select) {
            sender.sendMessage(ChatColor.RED + "A default value is required for selects");
            return true;
        }

        String name = args[0];
        String defaultValue = null;
        String displayName = name;

        if (attributeRepo.getAttribute(name).isPresent()) {
            sender.sendMessage(ChatColor.RED + "Attribute already exists");
            return true;
        }

        if (type != AttributeType.Select && selectRepo.getSelect(name).isPresent()) {
            String message = ChatColor.RED +
                "Cannot create attribute because a select exists with the same name " +
                "(/rpengine attribute addselect)";
            sender.sendMessage(message);
            return true;
        }

        if (args.length > 1) {
            defaultValue = args[1];
        }

        if (args.length > 2) {
            displayName = args[2];
        }

        attributeRepo.createAttributeAsync(name, type, displayName, defaultValue, r ->
            sender.sendMessage(Commands.mapResult(r, "Attribute added"))
        );

        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        attributeRepo.removeAttributeAsync(name, r ->
            sender.sendMessage(Commands.mapResult(r, "Attribute removed"))
        );

        return true;
    }

    private boolean handleSetDefault(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String name = args[0];
        String defaultValue = args[1];

        attributeRepo.setDefaultAsync(name, defaultValue, r ->
            sender.sendMessage(Commands.mapResult(r, "Attribute updated"))
        );

        return true;
    }

    private boolean handleSetDisplay(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String name = args[0];
        String display = args[1];

        attributeRepo.setDisplayAsync(name, display, r ->
            sender.sendMessage(Commands.mapResult(r, "Attribute updated"))
        );

        return true;
    }

    private boolean handleSetFormat(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String name = args[0];
        String formatString = String.join(" ", StringUtils.splice(args, 1));

        attributeRepo.setFormatAsync(name, formatString, r ->
            sender.sendMessage(Commands.mapResult(r, "Attribute updated"))
        );

        return true;
    }

    private boolean handleSetIdentity(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        attributeRepo.setIdentityAsync(name, r ->
            sender.sendMessage(Commands.mapResult(r, "Attribute updated"))
        );

        return true;
    }

    private boolean handleClearIdentity(CommandSender sender) {
        attributeRepo.clearIdentityAsync(r ->
            sender.sendMessage(Commands.mapResult(r, "Identity cleared"))
        );
        return true;
    }

    private boolean handleSetMarker(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        attributeRepo.setMarkerAsync(name, r ->
            sender.sendMessage(Commands.mapResult(r, "Attribute updated"))
        );

        return true;
    }

    private boolean handleClearMarker(CommandSender sender) {
        attributeRepo.clearMarkerAsync(r ->
            sender.sendMessage(Commands.mapResult(r, "Marker cleared"))
        );
        return true;
    }

    private boolean handleSetTitle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        attributeRepo.setTitleAsync(name, r ->
            sender.sendMessage(Commands.mapResult(r, "Attribute updated"))
        );

        return true;
    }

    private boolean handleClearTitle(CommandSender sender) {
        attributeRepo.clearTitleAsync(r ->
            sender.sendMessage(Commands.mapResult(r, "Title cleared"))
        );

        return true;
    }
}
