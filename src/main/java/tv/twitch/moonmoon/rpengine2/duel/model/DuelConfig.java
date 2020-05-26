package tv.twitch.moonmoon.rpengine2.duel.model;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerConfig;

import java.time.Instant;
import java.util.Objects;

/**
 * Manages player data related to duels
 */
public class DuelConfig implements RpPlayerConfig {

    private final int id;
    private final Instant created;
    private final int playerId;
    private final boolean readRules;

    public DuelConfig(int id, Instant created, int playerId, boolean readRules) {
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

    /**
     * Returns true if the player has read the duel rules
     *
     * @return True if has read rules
     */
    public boolean hasReadRules() {
        return readRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuelConfig that = (DuelConfig) o;
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
