package tv.twitch.moonmoon.rpengine2.spigot.util;

import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;

import javax.inject.Inject;
import java.util.Objects;
import java.util.logging.Logger;

public class SpigotMessenger implements Messenger {

    private final Logger log;

    @Inject
    public SpigotMessenger(@PluginLogger Logger log) {
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public void warn(String message) {
        log.warning(message);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }
}
