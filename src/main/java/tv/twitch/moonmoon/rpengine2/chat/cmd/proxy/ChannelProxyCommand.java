package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.cmd.CoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.Optional;

public interface ChannelProxyCommand extends CoreCommandExecutor {

    Chat getChat();

    RpPlayerRepo getPlayerRepo();

    String getNotConfiguredMessage();

    String getChannelName();

    @Override
    default boolean handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        Chat chat = getChat();
        RpPlayerRepo playerRepo = getPlayerRepo();
        ChatChannel channel = chat.getChannel(getChannelName()).orElse(null);
        Result<RpPlayer> p = playerRepo.getPlayer((Player) sender);

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }

        if (channel == null) {
            sender.sendMessage(getNotConfiguredMessage());
            return true;
        }

        return onSuccess(p.get(), channel, sender, args);
    }

    boolean onSuccess(RpPlayer player, ChatChannel channel, CommandSender sender, String[] args);
}
