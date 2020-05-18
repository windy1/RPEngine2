package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.cmd.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.cmd.CoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

public interface ChannelProxyCommand extends CoreCommandExecutor {

    Chat getChat();

    CommandPlayerParser getPlayerParser();

    String getNotConfiguredMessage();

    String getChannelName();

    @Override
    default boolean handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        Chat chat = getChat();
        ChatChannel channel = chat.getChannel(getChannelName()).orElse(null);
        RpPlayer player = getPlayerParser().parse(sender).orElse(null);

        if (player == null) {
            return true;
        }

        if (channel == null) {
            sender.sendMessage(getNotConfiguredMessage());
            return true;
        }

        return onSuccess(player, channel, sender, args);
    }

    boolean onSuccess(RpPlayer player, ChatChannel channel, CommandSender sender, String[] args);
}
