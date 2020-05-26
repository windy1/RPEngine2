package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

public interface Dueler {
    /**
     * Returns the underlying {@link RpPlayer}
     *
     * @return Player
     */
    RpPlayer getPlayer();

    /**
     * Returns the original amount of health the player had before starting a duel
     *
     * @return Original health
     */
    double getOriginalHealth();

    /**
     * Resets this player to their original state before the duel
     */
    void resetPlayer();
}
