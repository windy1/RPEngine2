package tv.twitch.moonmoon.rpengine2.data.player;

import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Manages the {@link RpPlayer} and
 * {@link RpPlayerAttribute} models
 */
public interface RpPlayerRepo extends Repo {

    /**
     * Returns a set of all loaded players
     *
     * @return Set of loaded players
     */
    Set<RpPlayer> getPlayers();

    /**
     * Returns the {@link RpPlayer} instance if loaded or creates a new instance in the database
     * and memory if not found (with a possible error)
     *
     * @param player player to lookup
     * @return Result of player lookup
     */
    Result<RpPlayer> getPlayer(UUID player);

    /**
     * Returns the {@link RpPlayer} with the specified name but does not attempt to create one if
     * not found
     *
     * @param name Player name
     * @return Player if found, empty otherwise
     */
    Optional<RpPlayer> getLoadedPlayer(String name);

    /**
     * Returns the player's `identity`, or, `display name` within the plugin
     *
     * @param player Player to get identity of
     * @return Player identity
     */
    String getIdentity(RpPlayer player);

    /**
     * Returns a player's plain identity (no prefix, title, etc) as marked by
     * `/rpengine at setident {attribute}`
     *
     * @param player Player to get identity of
     * @return Player identity
     */
    String getIdentityPlain(RpPlayer player);

    /**
     * Returns the player's prefix as marked by `/rpengine at setmarker {attribute}`
     *
     * @param player Player
     * @return Player prefix
     */
    String getPrefix(RpPlayer player);

    /**
     * Returns the player's title as marked by `/rpengine at settitle {attribute}`
     *
     * @param player Player
     * @return Player title
     */
    String getTitle(RpPlayer player);

    /**
     * Sets the specified attribute for the specified player asynchronously
     *
     * @param player Player to set attribute for
     * @param attributeId Attribute ID
     * @param value Attribute value
     * @param callback Callback invoked upon completion
     */
    void setAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Callback<Void> callback
    );

    /**
     * Removes all player attributes of the specified attribute ID.
     *
     * @param attributeId Attribute to remove
     * @param callback Callback invoked upon completion
     */
    void removeAttributesAsync(int attributeId, Callback<Void> callback);

    /**
     * Overwrites a player's start time for their current session (used to calculate `/played`)
     *
     * @param player Player
     */
    void startSessionAsync(RpPlayer player);

    /**
     * Clears a player's start time for their current session
     *
     * @param player Player to clear session for
     */
    void clearSession(RpPlayer player);

    /**
     * Sets the specified player's play time
     *
     * @param player Player
     * @param duration Play time
     */
    void setPlayed(RpPlayer player, Duration duration);

    /**
     * Reloads all players. Under most circumstances, there is little reason you should ever need
     * to call this.
     *
     * @return Result of reload
     */
    Result<Void> reloadPlayers();

    /**
     * Handles shutdown logic
     */
    void shutdown();
}
