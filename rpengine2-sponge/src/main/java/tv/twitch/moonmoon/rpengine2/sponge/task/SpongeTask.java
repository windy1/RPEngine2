package tv.twitch.moonmoon.rpengine2.sponge.task;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.task.Task;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SpongeTask implements Task {

    private final RpEngine2 plugin;
    private SpongeExecutorService task;

    public SpongeTask(RpEngine2 plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void setInterval(Runnable r, long delayMillis, long periodMillis) {
        if (task != null) {
            throw new IllegalStateException();
        }

        task = Sponge.getScheduler().createSyncExecutor(plugin);
        task.scheduleAtFixedRate(r, delayMillis, periodMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setIntervalAsync(Runnable r, long delayMillis, long periodMillis) {
        if (task != null) {
            throw new IllegalStateException();
        }

        task = Sponge.getScheduler().createAsyncExecutor(plugin);
        task.scheduleAtFixedRate(r, delayMillis, periodMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isCancelled() {
        return task == null || task.isShutdown();
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.shutdown();
        }
    }
}
