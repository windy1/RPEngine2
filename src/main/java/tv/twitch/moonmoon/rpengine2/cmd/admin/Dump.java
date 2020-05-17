package tv.twitch.moonmoon.rpengine2.cmd.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatChannelConfig;
import tv.twitch.moonmoon.rpengine2.cmd.Commands;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class Dump {

    private static final List<CommandUsage> USAGES = new ArrayList<>();
    private static final Help HELP = new Help(
        "Dump Sub Commands: ",
        "RPEngine raw data",
        USAGES
    );

    static {
        USAGES.add(new CommandUsage("player", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("attribute", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("select", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("channel", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("channelconfig", Arrays.asList(
            new ArgumentLabel("player", true),
            new ArgumentLabel("channel", true)
        )));

        USAGES.add(new CommandUsage("players"));
        USAGES.add(new CommandUsage("attributes"));
        USAGES.add(new CommandUsage("selects"));
        USAGES.add(new CommandUsage("channels"));
        USAGES.add(new CommandUsage("channelconfigs"));
    }

    private final RpPlayerRepo playerRepo;
    private final AttributeRepo attributeRepo;
    private final SelectRepo selectRepo;
    private final ChatChannelConfigRepo channelConfigRepo;
    private final Chat chat;

    @Inject
    public Dump(
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        Optional<ChatChannelConfigRepo> channelConfigRepo,
        Optional<Chat> chat
    ) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.channelConfigRepo = channelConfigRepo.orElse(null);
        this.chat = chat.orElse(null);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            return HELP.handle(sender, new String[] { "help" });
        }

        switch (args[0]) {
            case "player":
            case "p":
                return handleDumpPlayer(sender, StringUtils.splice(args, 1));
            case "players":
            case "ps": {
                sender.sendMessage(playerRepo.getPlayers().toString());
                return true;
            }
            case "attribute":
            case "at":
                return handleDumpAttribute(sender, StringUtils.splice(args, 1));
            case "attributes":
            case "ats": {
                sender.sendMessage(attributeRepo.getAttributes().toString());
                return true;
            }
            case "selects":
            case "sels": {
                sender.sendMessage(selectRepo.getSelects().toString());
                return true;
            }
            case "select":
            case "sel":
                return handleDumpSelect(sender, StringUtils.splice(args, 1));
            case "channels":
            case "chans":
                return handleDumpChannels(sender);
            case "channel":
            case "chan":
                return handleDumpChannel(sender, StringUtils.splice(args, 1));
            case "channelconfigs":
            case "chanconfigs":
                return handleDumpChannelConfigs(sender);
            case "channelconfig":
            case "chanconfig":
                return handleDumpChannelConfig(sender, StringUtils.splice(args, 1));
            default:
                return false;
        }
    }

    private boolean handleDumpPlayer(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        Optional<RpPlayer> p = playerRepo.getPlayer(name);
        if (!p.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Player not found");
        } else {
            sender.sendMessage(p.get().toString());
        }

        return true;
    }

    private boolean handleDumpAttribute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        Optional<Attribute> a = attributeRepo.getAttribute(name);
        if (!a.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Attribute not found");
        } else {
            sender.sendMessage(a.get().toString());
        }

        return true;
    }

    private boolean handleDumpSelect(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        Optional<Select> s = selectRepo.getSelect(name);
        if (!s.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Select not found");
        } else {
            sender.sendMessage(s.get().toString());
        }

        return true;
    }

    private boolean handleDumpChannels(CommandSender sender) {
        if (chat == null) {
            sender.sendMessage(ChatColor.RED + "Chat module disabled");
            return true;
        }

        sender.sendMessage(chat.getChannels().toString());

        return true;
    }

    private boolean handleDumpChannel(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (chat == null) {
            sender.sendMessage(ChatColor.RED + "Chat module disabled");
            return true;
        }

        ChatChannel channel = chat.getChannel(args[0]).orElse(null);

        if (channel == null) {
            sender.sendMessage(ChatColor.RED + "Channel not found");
        } else {
            sender.sendMessage(channel.toString());
        }

        return true;
    }

    private boolean handleDumpChannelConfigs(CommandSender sender) {
        if (channelConfigRepo == null) {
            sender.sendMessage(ChatColor.RED + "Chat module disabled");
            return true;
        }

        sender.sendMessage(channelConfigRepo.getConfigs().toString());

        return true;
    }

    private boolean handleDumpChannelConfig(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        if (channelConfigRepo == null || chat == null) {
            sender.sendMessage(ChatColor.RED + "Chat module disabled");
            return true;
        }

        Optional<RpPlayer> p = playerRepo.getPlayer(args[0]);
        Optional<ChatChannel> c = chat.getChannel(args[1]);
        RpPlayer player;
        ChatChannel channel;

        if (!p.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }
        player = p.get();

        if (!c.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Channel not found");
            return true;
        }
        channel = c.get();

        Result<ChatChannelConfig> config = channelConfigRepo.getConfig(player, channel);
        sender.sendMessage(Commands.mapResult(config, config.get().toString()));

        return true;
    }
}
