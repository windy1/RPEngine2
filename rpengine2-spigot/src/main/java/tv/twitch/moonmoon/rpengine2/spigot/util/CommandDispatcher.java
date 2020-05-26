package tv.twitch.moonmoon.rpengine2.spigot.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class CommandDispatcher {

    private final ConcurrentLinkedQueue<CommandExecution> queue = new ConcurrentLinkedQueue<>();
    private final BukkitTask task;

    @Inject
    public CommandDispatcher(Plugin plugin) {
        task = Bukkit.getScheduler().runTaskTimer(
            Objects.requireNonNull(plugin),
            this::flushQueue,
            0, 10
        );
    }

    public void add(UUID playerId, String command) {
        queue.add(new CommandExecution(playerId, command));
    }

    private void flushQueue() {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().execute();
            }
        }
    }

    static class CommandExecution {

        final UUID playerId;
        final String command;

        CommandExecution(UUID playerId, String command) {
            this.playerId = Objects.requireNonNull(playerId);
            this.command = Objects.requireNonNull(command);
        }

        void execute() {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.performCommand(command);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!task.isCancelled()) {
            task.cancel();
        }
    }
}
