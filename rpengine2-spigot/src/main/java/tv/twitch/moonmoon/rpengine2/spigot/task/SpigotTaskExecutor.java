package tv.twitch.moonmoon.rpengine2.spigot.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.task.TaskExecutor;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotTaskExecutor implements TaskExecutor {

    private final Plugin plugin;

    @Inject
    public SpigotTaskExecutor(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void execute(Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, r);
    }
}
