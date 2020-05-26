package tv.twitch.moonmoon.rpengine2.duel.data;

import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Set;

public interface DuelConfigDbo {
    Result<Set<DuelConfig>> selectConfigs();

    Result<DuelConfig> selectConfig(int playerId);

    Result<Long> insertConfig(int playerId);

    void setReadRulesAsync(int playerId, Callback<Void> callback);
}
