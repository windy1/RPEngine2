package tv.twitch.moonmoon.rpengine2.duel.data;

import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import java.util.Optional;

public interface DuelConfigRepo extends Repo {

    Optional<DuelConfig> getConfig(RpPlayer player);
}
