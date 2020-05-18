package tv.twitch.moonmoon.rpengine2.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.OptionalBinder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepoImpl;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepoImpl;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepoImpl;

import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

public class CoreModule extends AbstractModule {

    private final JavaPlugin plugin;

    public CoreModule(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Provides
    public JavaPlugin provideJavaPlugin() {
        return plugin;
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

    @Override
    protected void configure() {
        bind(RpPlayerRepo.class).to(RpPlayerRepoImpl.class);
        bind(AttributeRepo.class).to(AttributeRepoImpl.class);
        bind(SelectRepo.class).to(SelectRepoImpl.class);

        OptionalBinder.newOptionalBinder(binder(), Chat.class);
        OptionalBinder.newOptionalBinder(binder(), ChatChannelConfigRepo.class);

        if (plugin.getConfig().getBoolean("chat.enabled")) {
            install(new ChatModule());
        }
    }
}
