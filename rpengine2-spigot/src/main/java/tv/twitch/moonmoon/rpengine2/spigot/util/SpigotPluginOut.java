package tv.twitch.moonmoon.rpengine2.spigot.util;

import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.PluginOut;

import javax.inject.Inject;
import java.util.Objects;
import java.util.logging.Logger;

public class SpigotPluginOut implements PluginOut {

    private final Logger log;

    @Inject
    public SpigotPluginOut(@PluginLogger Logger log) {
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public void warn(String message) {
        log.warning(message);
    }
}
