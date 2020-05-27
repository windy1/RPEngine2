package tv.twitch.moonmoon.rpengine2.sponge.util;

import org.slf4j.Logger;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.PluginOut;

import javax.inject.Inject;
import java.util.Objects;

public class SpongePluginOut implements PluginOut {

    private final Logger log;

    @Inject
    public SpongePluginOut(@PluginLogger Logger log) {
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }
}
