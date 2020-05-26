package tv.twitch.moonmoon.rpengine2.model.player;

import tv.twitch.moonmoon.rpengine2.model.Model;

/**
 * Represents a model that stores player data
 */
public interface RpPlayerConfig extends Model {

    /**
     * Returns the Player ID of this config
     *
     * @return Player ID
     */
    int getPlayerId();
}
