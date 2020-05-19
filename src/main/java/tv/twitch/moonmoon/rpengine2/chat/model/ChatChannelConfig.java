package tv.twitch.moonmoon.rpengine2.chat.model;

import java.time.Instant;
import java.util.Objects;

public class ChatChannelConfig {

    private final int id;
    private final Instant created;
    private final String channelName;
    private final int playerId;
    private final boolean muted;

    public ChatChannelConfig(
        int id,
        Instant created,
        String channelName,
        int playerId,
        boolean muted
    ) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.channelName = Objects.requireNonNull(channelName);
        this.playerId = playerId;
        this.muted = muted;
    }

    public int getId() {
        return id;
    }

    public Instant getCreated() {
        return created;
    }

    public String getChannelName() {
        return channelName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isMuted() {
        return muted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannelConfig that = (ChatChannelConfig) o;
        return playerId == that.playerId &&
            Objects.equals(channelName, that.channelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelName, playerId);
    }

    @Override
    public String toString() {
        return "ChatChannelConfig{" +
            "id=" + id +
            ", created=" + created +
            ", channelName='" + channelName + '\'' +
            ", playerId=" + playerId +
            ", muted=" + muted +
            '}';
    }
}
