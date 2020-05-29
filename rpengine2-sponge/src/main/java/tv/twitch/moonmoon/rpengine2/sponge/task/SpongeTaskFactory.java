package tv.twitch.moonmoon.rpengine2.sponge.task;

import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.task.Task;
import tv.twitch.moonmoon.rpengine2.task.TaskFactory;

import javax.inject.Inject;
import java.util.Objects;

public class SpongeTaskFactory implements TaskFactory {

    private final RpEngine2 plugin;

    @Inject
    public SpongeTaskFactory(RpEngine2 plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public Task newInstance() {
        return new SpongeTask(plugin);
    }
}
