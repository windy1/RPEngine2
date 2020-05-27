package tv.twitch.moonmoon.rpengine2.spigot.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.util.AsyncExecutor;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotAsyncExecutor implements AsyncExecutor {

    private final Plugin plugin;

    @Inject
    public SpigotAsyncExecutor(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void execute(Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, r);
    }
}
