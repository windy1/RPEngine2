package tv.twitch.moonmoon.rpengine2.sponge.task;

import org.spongepowered.api.Sponge;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.task.TaskExecutor;

import javax.inject.Inject;
import java.util.Objects;

public class SpongeTaskExecutor implements TaskExecutor {

    private final RpEngine2 plugin;

    @Inject
    public SpongeTaskExecutor(RpEngine2 plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void execute(Runnable r) {
        Sponge.getScheduler().createAsyncExecutor(plugin).execute(r);
    }
}
