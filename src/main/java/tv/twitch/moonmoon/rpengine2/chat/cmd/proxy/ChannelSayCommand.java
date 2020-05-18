package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

public interface ChannelSayCommand extends ChannelProxyCommand {

    @Override
    default boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }
        return ChannelProxyCommand.super.handle(sender, args);
    }

    @Override
    default boolean onSuccess(
        RpPlayer player,
        ChatChannel channel,
        CommandSender sender,
        String[] args
    ) {
        String message = String.join(" ", args);
        getChat().sendMessage(player, channel, message);
        return true;
    }
}
