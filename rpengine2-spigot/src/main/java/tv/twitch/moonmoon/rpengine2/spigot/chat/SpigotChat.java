package tv.twitch.moonmoon.rpengine2.spigot.chat;

import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

/**
 * Module responsible for managing chat-related functionality
 */
public interface SpigotChat extends tv.twitch.moonmoon.rpengine2.chat.Chat {

    void toggleMutedAsync(RpPlayer player, ChatChannel channel, CommandSender sender);
}
