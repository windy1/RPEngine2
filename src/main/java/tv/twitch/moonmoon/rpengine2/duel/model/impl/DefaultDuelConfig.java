package tv.twitch.moonmoon.rpengine2.duel.model.impl;

import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;

import java.time.Instant;
import java.util.Objects;

public class DefaultDuelConfig implements DuelConfig {

    private final int id;
    private final Instant created;
    private final int playerId;
    private final boolean readRules;

    public DefaultDuelConfig(int id, Instant created, int playerId, boolean readRules) {
        this.id = id;
        this.created = Objects.requireNonNull(created);
        this.playerId = playerId;
        this.readRules = readRules;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    @Override
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public boolean hasReadRules() {
        return readRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultDuelConfig that = (DefaultDuelConfig) o;
        return playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public String toString() {
        return "DuelConfig{" +
            "id=" + id +
            ", created=" + created +
            ", playerId=" + playerId +
            ", readRules=" + readRules +
            '}';
    }
}
