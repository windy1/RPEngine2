package tv.twitch.moonmoon.rpengine2.duel.data;

import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.Set;

public interface DuelConfigRepo extends Repo {

    Result<DuelConfig> getConfig(RpPlayer player);

    Set<DuelConfig> getConfigs();

    void setRulesReadAsync(RpPlayer player);
}
