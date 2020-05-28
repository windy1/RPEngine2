package tv.twitch.moonmoon.rpengine2.sponge.util;

import org.slf4j.Logger;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;

import javax.inject.Inject;
import java.util.Objects;

public class SpongeMessenger implements Messenger {

    private final Logger log;

    @Inject
    public SpongeMessenger(@PluginLogger Logger log) {
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }
}
