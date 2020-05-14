package tv.twitch.moonmoon.rpengine2.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

public class RpModule extends AbstractModule {

    private final Plugin plugin;

    public RpModule(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Provides
    public Plugin providePlugin() {
        return plugin;
    }

    @Provides
    @PluginLogger
    public static Logger provideLogger(Plugin plugin) {
        return plugin.getLogger();
    }

    @Provides
    @DbPath
    public static Path provideDbPath(Plugin plugin) {
        return plugin.getDataFolder().toPath().resolve("plugin.db");
    }
}
