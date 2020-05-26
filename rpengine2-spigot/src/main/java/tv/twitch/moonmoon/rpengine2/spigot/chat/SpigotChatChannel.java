package tv.twitch.moonmoon.rpengine2.spigot.chat;

import net.md_5.bungee.api.ChatColor;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;

import java.util.Objects;
import java.util.Optional;

/**
 * A chat-channel of communication
 */
public class SpigotChatChannel implements ChatChannel {

    private final String name;
    private final int range;
    private final String prefix;
    private final String permission;
    private final ChatColor messageColor;

    public SpigotChatChannel(
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
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
        SpigotChatChannel that = (SpigotChatChannel) o;
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
