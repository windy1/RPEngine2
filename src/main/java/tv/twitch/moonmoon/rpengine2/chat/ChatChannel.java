package tv.twitch.moonmoon.rpengine2.chat;

import java.util.Optional;

public interface ChatChannel {
    /**
     * Returns this channel unique ID
     *
     * @return Channel ID
     */
    String getName();

    /**
     * Returns the amount of blocks player can hear you from when chatting in this channel
     *
     * @return Block range
     */
    int getRange();

    /**
     * Returns the prefix inserted into each message in this channel
     *
     * @return Channel prefix
     */
    String getPrefix();

    /**
     * Returns the permission string a player is required to have to send and receive messages in
     * this channel.
     *
     * @return Permission string
     */
    Optional<String> getPermission();
}
