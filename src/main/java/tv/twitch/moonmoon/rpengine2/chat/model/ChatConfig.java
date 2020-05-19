package tv.twitch.moonmoon.rpengine2.chat.model;

import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class ChatConfig {

    private final int id;
    private final Instant created;
    private final int playerId;
    private final ChatChannel channel;

    public ChatConfig(int id, Instant created, int playerId, ChatChannel channel) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.playerId = playerId;
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public Instant getCreated() {
        return Objects.requireNonNull(created);
    }

    public int getPlayerId() {
        return playerId;
    }

    public Optional<ChatChannel> getChannel() {
        return Optional.ofNullable(channel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatConfig that = (ChatConfig) o;
        return id == that.id &&
            playerId == that.playerId &&
            Objects.equals(created, that.created) &&
            Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public String toString() {
        return "ChatConfig{" +
            "id=" + id +
            ", created=" + created +
            ", playerId=" + playerId +
            ", channel=" + channel +
            '}';
    }
}
