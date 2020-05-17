package tv.twitch.moonmoon.rpengine2.chat;

import java.util.Objects;
import java.util.Optional;

public class ChatChannel {

    private final String name;
    private final int range;
    private final String prefix;

    public ChatChannel(String name, int range, String prefix) {
        this.name = Objects.requireNonNull(name);
        this.range = range;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public int getRange() {
        return range;
    }

    public Optional<String> getPrefix() {
        return Optional.of(prefix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel channel = (ChatChannel) o;
        return range == channel.range &&
            Objects.equals(name, channel.name) &&
            Objects.equals(prefix, channel.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Channel{" +
            "name='" + name + '\'' +
            ", range=" + range +
            ", prefix='" + prefix + '\'' +
            '}';
    }
}
