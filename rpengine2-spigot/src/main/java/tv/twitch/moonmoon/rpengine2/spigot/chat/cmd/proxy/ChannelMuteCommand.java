package tv.twitch.moonmoon.rpengine2.spigot.chat.cmd.proxy;

import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.chat.SpigotChat;

public interface ChannelMuteCommand extends ChannelProxyCommand {

    @Override
    default boolean onSuccess(RpPlayer player, ChatChannel channel, CommandSender sender, String[] args) {
        ((SpigotChat) getChat()).toggleMutedAsync(player, channel, sender);
        return true;
    }
}
