package tv.twitch.moonmoon.rpengine2.chat;

import net.md_5.bungee.api.ChatColor;

import java.util.Objects;
import java.util.Optional;

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

    public String getName() {
        return name;
    }

    public int getRange() {
        return range;
    }

    public String getPrefix() {
        return prefix;
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public ChatColor getMessageColor() {
        return messageColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel that = (ChatChannel) o;
        return range == that.range &&
            Objects.equals(name, that.name) &&
            Objects.equals(prefix, that.prefix) &&
            Objects.equals(permission, that.permission) &&
            messageColor == that.messageColor;
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
