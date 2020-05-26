package tv.twitch.moonmoon.rpengine2.data.player;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface RpPlayerDbo {
    Result<Void> clearSessions();

    Result<Set<RpPlayer>> selectPlayers();

    Result<Long> insertPlayer(String name, UUID id);

    Result<RpPlayer> selectPlayer(UUID playerId);

    void insertPlayerAttributeAsync(
            int playerId,
            int attributeId,
            Object value,
            Callback<Void> callback
    );

    void updatePlayerAttributeAsync(
            int playerId,
            int attributeId,
            Object value,
            Callback<Void> callback
    );

    Result<Void> updatePlayerAttribute(int playerId, int attributeId, Object value);

    void deletePlayerAttributesAsync(int attributeId, Callback<Void> callback);

    void setSessionAsync(int playerId, Instant sessionStart, Callback<Void> callback);

    Result<Void> setSession(int playerId, Instant sessionStart);

    Result<Void> setPlayed(int playerId, Duration played);
}
