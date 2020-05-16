package tv.twitch.moonmoon.rpengine2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.cmd.Commands;
import tv.twitch.moonmoon.rpengine2.data.DataManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public class Bootstrap {

    private final JavaPlugin plugin;
    private final RpListener listener;
    private final Commands commands;
    private final DataManager dataManager;

    @Inject
    public Bootstrap(
        JavaPlugin plugin,
        RpListener listener,
        Commands commands,
        DataManager dataManager
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.listener = Objects.requireNonNull(listener);
        this.commands = Objects.requireNonNull(commands);
        this.dataManager = Objects.requireNonNull(dataManager);
    }

    public void init() {
        plugin.saveDefaultConfig();
        commands.register();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(listener, plugin);

        if (dataManager.init().getError().isPresent()) {
            pluginManager.disablePlugin(plugin);
        }
    }
}
