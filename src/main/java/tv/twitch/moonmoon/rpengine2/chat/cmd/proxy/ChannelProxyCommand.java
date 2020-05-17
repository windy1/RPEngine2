package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.Optional;

public interface ChannelProxyCommand extends CommandExecutor {

    Chat getChat();

    RpPlayerRepo getPlayerRepo();

    String getNotConfiguredMessage();

    String getChannelName();

    default boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        Chat chat = getChat();
        RpPlayerRepo playerRepo = getPlayerRepo();
        ChatChannel whisperChannel = chat.getChannel(getChannelName()).orElse(null);
        Result<RpPlayer> p = playerRepo.getPlayer((Player) sender);

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }

        if (whisperChannel == null) {
            sender.sendMessage(getNotConfiguredMessage());
            return true;
        }

        String message = String.join(" ", args);
        chat.sendMessage(p.get(), whisperChannel, message);

        return true;
    }

    @Override
    default boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return handle(sender, args);
    }
}
