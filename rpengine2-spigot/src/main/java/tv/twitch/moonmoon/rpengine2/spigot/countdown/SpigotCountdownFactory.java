package tv.twitch.moonmoon.rpengine2.spigot.countdown;

import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.countdown.Countdown;
import tv.twitch.moonmoon.rpengine2.countdown.CountdownFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SpigotCountdownFactory implements CountdownFactory {

    private final Plugin plugin;

    @Inject
    public SpigotCountdownFactory(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public Countdown newInstance(Set<UUID> playerIds, int timeSecs) {
        return SpigotCountdown.from(plugin.getConfig(), playerIds, timeSecs);
    }
}
