package tv.twitch.moonmoon.rpengine2;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.OptionalBinder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatModule;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLogModule;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepoImpl;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepoImpl;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepoImpl;
import tv.twitch.moonmoon.rpengine2.duel.DuelModule;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.nms.RpProtocolLibModule;
import tv.twitch.moonmoon.rpengine2.nms.RpProtocolLib;
import tv.twitch.moonmoon.rpengine2.nte.RpNametagEditModule;
import tv.twitch.moonmoon.rpengine2.nte.RpNametagEdit;
import tv.twitch.moonmoon.rpengine2.util.DbPath;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;

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
        OptionalBinder.newOptionalBinder(binder(), ChatConfigRepo.class);
        OptionalBinder.newOptionalBinder(binder(), ChatChannelConfigRepo.class);

        OptionalBinder.newOptionalBinder(binder(), RpProtocolLib.class);

        OptionalBinder.newOptionalBinder(binder(), RpNametagEdit.class);

        OptionalBinder.newOptionalBinder(binder(), Duels.class);
        OptionalBinder.newOptionalBinder(binder(), DuelConfigRepo.class);

        OptionalBinder.newOptionalBinder(binder(), CombatLog.class);

        FileConfiguration config = plugin.getConfig();

        if (config.getBoolean("chat.enabled")) {
            install(new ChatModule());
        }

        if (config.getBoolean("duels.enabled")) {
            install(new DuelModule());
        }

        if (config.getBoolean("combatlog.enabled")) {
            install(new CombatLogModule(plugin));
        }

        try {
            Class.forName("com.comphenix.protocol.ProtocolLibrary");
            install(new RpProtocolLibModule());
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("com.nametagedit.plugin.NametagEdit");
            install(new RpNametagEditModule());
        } catch (ClassNotFoundException ignored) {
        }
    }
}
