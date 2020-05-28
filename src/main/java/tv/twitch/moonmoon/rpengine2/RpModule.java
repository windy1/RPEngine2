package tv.twitch.moonmoon.rpengine2;

import tv.twitch.moonmoon.rpengine2.util.Result;

/**
 * Represents a feature-boundary for the plugin
 */
public interface RpModule {

    /**
     * Initializes this module
     *
     * @return Result of initialization
     */
    Result<Void> init();
}
