package tv.twitch.moonmoon.rpengine2.spigot.task;

import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.task.Task;
import tv.twitch.moonmoon.rpengine2.task.TaskFactory;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotTaskFactory implements TaskFactory {

    private final Plugin plugin;

    @Inject
    public SpigotTaskFactory(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public Task newInstance() {
        return new SpigotTask(plugin);
    }
}
