package tv.twitch.moonmoon.rpengine2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.Commands;
import tv.twitch.moonmoon.rpengine2.data.DataManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class Bootstrap {

    private final JavaPlugin plugin;
    private final CoreListener listener;
    private final Commands commands;
    private final DataManager dataManager;
    private final Chat chat;
    private final Logger log;

    @Inject
    public Bootstrap(
        JavaPlugin plugin,
        CoreListener listener,
        Commands commands,
        DataManager dataManager,
        Optional<Chat> chat
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.listener = Objects.requireNonNull(listener);
        this.commands = Objects.requireNonNull(commands);
        this.dataManager = Objects.requireNonNull(dataManager);
        this.chat = chat.orElse(null);
        log = plugin.getLogger();
    }

    public void init() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        commands.register();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(listener, plugin);

        if (dataManager.init().getError().isPresent()) {
            pluginManager.disablePlugin(plugin);
        }

        if (chat != null) {
            chat.load();
        }

        log.info("Done");
    }
}
