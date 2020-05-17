package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class ChannelCommand implements CommandExecutor {

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

        USAGES.add(new CommandUsage("join", Collections.singletonList(
            new ArgumentLabel("channel", true)
        )));
    }

    private final Chat chat;
    private final RpPlayerRepo playerRepo;
    private final ChatChannelConfigRepo channelConfigRepo;

    @Inject
    public ChannelCommand(
        Chat chat,
        RpPlayerRepo playerRepo,
        ChatChannelConfigRepo channelConfigRepo
    ) {
        this.chat = Objects.requireNonNull(chat);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.channelConfigRepo = Objects.requireNonNull(channelConfigRepo);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println("DEBUG-1");
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            HELP.handle(sender, new String[] { "help" });
            return true;
        }

        Player mcPlayer = (Player) sender;
        Result<RpPlayer> p = playerRepo.getPlayer(mcPlayer);
        RpPlayer player;

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }
        player = p.get();

        if (args.length == 0) {
            return handleGetChannel(sender, player);
        }

        System.out.println("DEBUG0");

        switch (args[0]) {
            case "mute":
            case "unmute":
                return handleMute(sender, StringUtils.splice(args, 1));
            case "join":
            case "j":
                System.out.println("DEBUG1");
                return handleJoin(sender, StringUtils.splice(args, 1));
            default:
                return false;
        }
    }

    private boolean handleGetChannel(CommandSender sender, RpPlayer player) {
        String channelName = player.getChatChannel()
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
        Result<RpPlayer> p = playerRepo.getPlayer((Player) sender);
        RpPlayer player;

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }
        player = p.get();
        
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
        Result<RpPlayer> p = playerRepo.getPlayer((Player) sender);

        if (channel == null) {
            sender.sendMessage(ChatColor.RED + "Channel not found");
            return true;
        }

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
        } else {
            chat.setChatChannelAsync(p.get(), channel);
        }

        return true;
    }
}
