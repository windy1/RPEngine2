package tv.twitch.moonmoon.rpengine2.chat;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.Set;

public interface Chat {
    /**
     * Returns the {@link ChatChannel} with the specified name
     *
     * @param name Channel name
     * @return Channel if found
     */
    Optional<ChatChannel> getChannel(String name);

    /**
     * Returns a set of all loaded {@link ChatChannel}
     *
     * @return All channels
     */
    Set<ChatChannel> getChannels();

    /**
     * Returns the {@link ChatChannel} a player is placed in if they are not currently in a channel
     * upon joining
     *
     * @return Default channel
     */
    Optional<ChatChannel> getDefaultChannel();

    /**
     * Sends a message to all the members in the player's channel
     *
     * @param sender Message sender
     * @param message Message to send
     * @return True if message was sent successfully
     */
    boolean sendMessage(RpPlayer sender, String message);

    /**
     * Sends a message to all the members in the specified channel
     *
     * @param sender Message sender
     * @param channel Channel to send message in
     * @param message Message to send
     * @return True if message was sent successfully
     */
    boolean sendMessage(RpPlayer sender, ChatChannel channel, String message);

    /**
     * Returns the speed (blocks per second) of the carrier pigeon
     *
     * @return Bird speed
     */
    int getBirdSpeed();

    /**
     * Sets a player's {@link ChatChannel} asynchronously
     *
     * @param player Player to set channel for
     * @param channel Channel to set
     */
    void setChatChannelAsync(RpPlayer player, ChatChannel channel);

    Result<Void> init();

    void handlePlayerJoined(RpPlayer player);
}
