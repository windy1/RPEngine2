package tv.twitch.moonmoon.rpengine2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.RpPlayerRepo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class Bootstrap {

    private final Plugin plugin;
    private final RpDb db;
    private final RpPlayerRepo playerRepo;
    private final RpListener listener;
    private final Logger log;

    @Inject
    public Bootstrap(Plugin plugin, RpDb db, RpPlayerRepo playerRepo, RpListener listener) {
        this.plugin = Objects.requireNonNull(plugin);
        this.db = Objects.requireNonNull(db);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.listener = Objects.requireNonNull(listener);
        log = plugin.getLogger();
    }

    public void init() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(listener, plugin);

        db.connectAsync(r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
                pluginManager.disablePlugin(plugin);
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
