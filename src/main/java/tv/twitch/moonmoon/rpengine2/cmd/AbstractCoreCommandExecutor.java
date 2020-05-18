package tv.twitch.moonmoon.rpengine2.cmd;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.util.*;

public abstract class AbstractCoreCommandExecutor implements CoreCommandExecutor {

    protected final Plugin plugin;
    private final Map<UUID, Instant> executions = new HashMap<>();

    public AbstractCoreCommandExecutor(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public Optional<Instant> getLastExecution(OfflinePlayer sender) {
        return Optional.ofNullable(executions.get(sender.getUniqueId()));
    }

    @Override
    public void setLastExecution(OfflinePlayer sender, Instant instant) {
        executions.put(sender.getUniqueId(), instant);
    }
}
