package tv.twitch.moonmoon.rpengine2.chat.cmd;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.chat.CommandChatConfigParser;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatConfig;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChannelCommand extends AbstractCoreCommandExecutor {

    private static final List<CommandUsage> USAGES = new ArrayList<>();
    private static final Help HELP = new Help(
        "Channel Sub Commands: ",
        "You feel as though chat channels hold the key to communication",
        USAGES
    );

    static {
        List<ArgumentLabel> muteArgs = Collections.singletonList(
            new ArgumentLabel("channel", true)
        );

        USAGES.add(new CommandUsage("mute", muteArgs));
        USAGES.add(new CommandUsage("unmute", muteArgs));
        USAGES.add(new CommandUsage("list"));

        USAGES.add(new CommandUsage("join", Collections.singletonList(
            new ArgumentLabel("channel", true)
        )));
    }

    private final Chat chat;
    private final CommandChatConfigParser configParser;
    private final CommandPlayerParser playerParser;

    @Inject
    public ChannelCommand(
        Plugin plugin,
        Chat chat,
        CommandChatConfigParser configParser,
        CommandPlayerParser playerParser
    ) {
        super(plugin);
        this.chat = Objects.requireNonNull(chat);
        this.configParser = Objects.requireNonNull(configParser);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            HELP.handle(sender, new String[] { "help" });
            return true;
        }

        RpPlayer player = playerParser.parse(sender).orElse(null);

        if (player == null) {
            return true;
        }

        if (args.length == 0) {
            return handleGetChannel(sender);
        }

        switch (args[0]) {
            case "mute":
            case "unmute":
                return handleMute(sender, StringUtils.splice(args, 1));
            case "join":
            case "j":
                return handleJoin(sender, StringUtils.splice(args, 1));
            case "list":
                return handleListChannels(sender);
            default:
                return false;
        }
    }

    private boolean handleGetChannel(CommandSender sender) {
        ChatConfig chatConfig = configParser.parse(sender).orElse(null);

        if (chatConfig == null) {
            return true;
        }

        String channelName = chatConfig.getChannel()
            .map(ChatChannel::getName)
            .orElseGet(() -> chat.getDefaultChannel()
                .map(ChatChannel::getName)
                .orElse("???")
            );

        String message = String.format(
            "%s%sCurrently chatting in [%s]",
            ChatColor.GRAY, ChatColor.ITALIC, channelName
        );

        sender.sendMessage(message);
        return true;
    }

    private boolean handleMute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String channelName = args[0];
        ChatChannel channel = chat.getChannel(channelName).orElse(null);
        RpPlayer player = playerParser.parse(sender).orElse(null);

        if (player == null) {
            return true;
        }
        
        if (channel == null) {
            sender.sendMessage(ChatColor.RED + "Channel not found");
            return true;
        }

        chat.toggleMutedAsync(player, channel, sender);

        return true;
    }

    private boolean handleJoin(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        ChatChannel channel = chat.getChannel(args[0]).orElse(null);
        RpPlayer p = playerParser.parse(sender).orElse(null);

        if (p == null) {
            return true;
        }

        if (channel == null) {
            sender.sendMessage(ChatColor.RED + "Channel not found");
            return true;
        }

        chat.setChatChannelAsync(p, channel);

        return true;
    }

    private boolean handleListChannels(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "Chat channels:");
        sender.sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + "Click a channel to join");
        for (ChatChannel channel : chat.getChannels()) {
            String channelName = channel.getName();
            TextComponent c = new TextComponent();
            TextComponent prefix = new TextComponent("# ");
            TextComponent nameTag = new TextComponent(channelName);

            prefix.setColor(net.md_5.bungee.api.ChatColor.BLUE);
            nameTag.setColor(net.md_5.bungee.api.ChatColor.GREEN);

            c.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND, "/channel join " + channelName
            ));

            c.addExtra(prefix);
            c.addExtra(nameTag);

            sender.spigot().sendMessage(c);
        }
        return true;
    }

    @Override
    public String getConfigPath() {
        return "chat.commands.channel";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
