package tv.twitch.moonmoon.rpengine2.spigot.chat.cmd.proxy;

import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

public interface ChannelJoinCommand extends ChannelProxyCommand {

    @Override
    default boolean onSuccess(RpPlayer player, ChatChannel channel, CommandSender sender, String[] args) {
        getChat().setChatChannelAsync(player, channel);
        return true;
    }
}
