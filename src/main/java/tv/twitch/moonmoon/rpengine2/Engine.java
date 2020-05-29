package tv.twitch.moonmoon.rpengine2;

import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import java.util.Optional;

public interface Engine {

    /**
     * Returns the {@link RpPlayerRepo} instance responsible for managing player data
     *
     * @return Player repo
     */
    RpPlayerRepo getPlayerRepo();

    /**
     * Returns the {@link AttributeRepo} instance responsible for managing attribute data
     *
     * @return Attribute repo
     */
    AttributeRepo getAttributeRepo();

    /**
     * Returns the {@link SelectRepo} instance responsible for managing select data
     *
     * @return Select repo
     */
    SelectRepo getSelectRepo();

    /**
     * Returns the {@link Chat} module instance if enabled, empty otherwise
     *
     * @return Chat module
     */
    Optional<Chat> getChatModule();

    /**
     * Returns the {@link Duels} module instance if enabled, empty otherwise
     *
     * @return Duels module
     */
    Optional<Duels> getDuelsModule();

    /**
     * Returns the {@link CombatLog} module instance if enabled, empty otherwise
     *
     * @return CombatLog module
     */
    Optional<CombatLog> getCombatLogModule();

    /**
     * Handles a new player joining the server
     *
     * @param player Player that joined
     */
    void handlePlayerJoined(RpPlayer player);

    /**
     * Handles a player leaving the server
     *
     * @param player Player that quit
     */
    void handlePlayerQuit(RpPlayer player);

    /**
     * Initializes the engine and prepares the plugin
     */
    void start();

    /**
     * Cleans up resources and handles any shutdown logic
     */
    void shutdown();
}
