package tv.twitch.moonmoon.rpengine2.combatlog;

import tv.twitch.moonmoon.rpengine2.util.Result;

/**
 * Module that manages combat-log related functionality
 */
public interface CombatLog {

//    /**
//     * Returns {@link ShowDamage} module if enabled, empty otherwise
//     *
//     * @return ShowDamage module
//     */
//    Optional<ShowDamage> getShowDamageModule();

    Result<Void> init();
}
