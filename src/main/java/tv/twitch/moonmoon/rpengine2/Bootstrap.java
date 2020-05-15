package tv.twitch.moonmoon.rpengine2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.cmd.Commands;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class Bootstrap {

    private final JavaPlugin plugin;
    private final RpDb db;
    private final RpPlayerRepo playerRepo;
    private final RpListener listener;
    private final Commands commands;
    private final Logger log;

    @Inject
    public Bootstrap(
        JavaPlugin plugin,
        RpDb db,
        RpPlayerRepo playerRepo,
        RpListener listener,
        Commands commands
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.db = Objects.requireNonNull(db);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.listener = Objects.requireNonNull(listener);
        this.commands = Objects.requireNonNull(commands);
        log = plugin.getLogger();
    }

    public void init() {
        plugin.saveDefaultConfig();
        commands.register();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(listener, plugin);

        db.connectAsync(r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
                // TODO: must do this on main thread
//                pluginManager.disablePlugin(plugin);
            } else {
                this.onDbConnect();
            }
        });
    }

    private void onDbConnect() {
        log.info("Connected to database");
        playerRepo.load();
    }
}
