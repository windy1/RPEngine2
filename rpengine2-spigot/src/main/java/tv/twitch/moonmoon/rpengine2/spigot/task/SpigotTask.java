package tv.twitch.moonmoon.rpengine2.spigot.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import tv.twitch.moonmoon.rpengine2.task.Task;

import java.util.Objects;

public class SpigotTask implements Task {

    private final Plugin plugin;
    private BukkitTask task;

    public SpigotTask(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void setInterval(Runnable r, long delayMillis, long periodMillis) {
        if (task != null) {
            throw new IllegalStateException();
        }

        long delayTicks = delayMillis / 1000 * 20;
        long periodTicks = periodMillis / 1000 * 20;
        task = Bukkit.getScheduler().runTaskTimer(plugin, r, delayTicks, periodTicks);
    }

    @Override
    public void setIntervalAsync(Runnable r, long delayMillis, long periodMillis) {
        if (task != null) {
            throw new IllegalStateException();
        }

        long delayTicks = delayMillis / 1000 * 20;
        long periodTicks = periodMillis / 1000 * 20;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(
            plugin, r, delayTicks, periodTicks
        );
    }

    @Override
    public boolean isCancelled() {
        return task == null || task.isCancelled();
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.cancel();
        }
    }
}
