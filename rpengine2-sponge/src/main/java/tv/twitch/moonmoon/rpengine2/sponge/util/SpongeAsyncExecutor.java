package tv.twitch.moonmoon.rpengine2.sponge.util;

import org.spongepowered.api.Sponge;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.util.AsyncExecutor;

import javax.inject.Inject;
import java.util.Objects;

public class SpongeAsyncExecutor implements AsyncExecutor {

    private final RpEngine2 plugin;

    @Inject
    public SpongeAsyncExecutor(RpEngine2 plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void execute(Runnable r) {
        Sponge.getScheduler().createAsyncExecutor(plugin).execute(r);
    }
}
