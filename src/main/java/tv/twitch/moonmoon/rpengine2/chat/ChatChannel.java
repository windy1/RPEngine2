package tv.twitch.moonmoon.rpengine2.chat;

import net.md_5.bungee.api.ChatColor;

import java.util.Objects;
import java.util.Optional;

/**
 * A chat-channel of communication
 */
public class ChatChannel {

    private final String name;
    private final int range;
    private final String prefix;
    private final String permission;
    private final ChatColor messageColor;

    public ChatChannel(
        String name,
        int range,
        String prefix,
        String permission,
        ChatColor messageColor
    ) {
        this.name = Objects.requireNonNull(name);
        this.range = range;
        this.prefix = Objects.requireNonNull(prefix);
        this.permission = permission;
        this.messageColor = Objects.requireNonNull(messageColor);
    }

    /**
     * Returns this channel unique ID
     *
     * @return Channel ID
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the amount of blocks player can hear you from when chatting in this channel
     *
     * @return Block range
     */
    public int getRange() {
        return range;
    }

    /**
     * Returns the prefix inserted into each message in this channel
     *
     * @return Channel prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the permission string a player is required to have to send and receive messages in
     * this channel.
     *
     * @return Permission string
     */
    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    /**
     * Returns the color of messages in this channel
     *
     * @return Message color
     */
    public ChatColor getMessageColor() {
        return messageColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel that = (ChatChannel) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ChatChannel{" +
            "name='" + name + '\'' +
            ", range=" + range +
            ", prefix='" + prefix + '\'' +
            ", permission='" + permission + '\'' +
            ", messageColor=" + messageColor +
            '}';
    }
}
