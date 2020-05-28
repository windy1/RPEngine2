package tv.twitch.moonmoon.rpengine2.duel.model;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerConfig;

/**
 * Manages player data related to duels
 */
public interface DuelConfig extends RpPlayerConfig {

    /**
     * Returns true if the player has read the duel rules
     *
     * @return True if has read rules
     */
    boolean hasReadRules();
}
