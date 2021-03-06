package tv.twitch.moonmoon.rpengine2.duel.data;

import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Set;

/**
 * Manages {@link DuelConfig} model
 */
public interface DuelConfigRepo extends Repo {

    /**
     * Returns or creates a {@link DuelConfig} for the specified {@link RpPlayer}
     *
     * @param player Player to get config for
     * @return Duel config
     */
    Result<DuelConfig> getConfig(RpPlayer player);

    /**
     * Returns a set of loaded {@link DuelConfig}s
     *
     * @return DuelConfigs
     */
    Set<DuelConfig> getConfigs();

    /**
     * Sets rules read for the specified player
     *
     * @param player Player who read the rules
     */
    void setRulesReadAsync(RpPlayer player);
}
