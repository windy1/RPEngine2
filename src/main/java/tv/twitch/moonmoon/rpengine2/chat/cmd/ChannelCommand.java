package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
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
    }

    private final Chat chat;
    private final RpPlayerRepo playerRepo;

    @Inject
    public ChannelCommand(Chat chat, RpPlayerRepo playerRepo) {
        this.chat = Objects.requireNonNull(chat);
        this.playerRepo = Objects.requireNonNull(playerRepo);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        switch (args[0]) {
            case "mute":
            case "unmute":
                return handleMute(sender, args);
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
        return true;
    }
}
