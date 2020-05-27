package tv.twitch.moonmoon.rpengine2.spigot;

import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.multibindings.OptionalBinder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.PluginModule;
import tv.twitch.moonmoon.rpengine2.chat.ChatModule;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLogModule;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerFactory;
import tv.twitch.moonmoon.rpengine2.model.select.OptionFactory;
import tv.twitch.moonmoon.rpengine2.spigot.chat.SpigotChatModule;
import tv.twitch.moonmoon.rpengine2.spigot.combatlog.SpigotCombatLogModule;
import tv.twitch.moonmoon.rpengine2.spigot.data.player.SpigotRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.spigot.data.select.SpigotOptionFactory;
import tv.twitch.moonmoon.rpengine2.spigot.duel.SpigotDuelsModule;
import tv.twitch.moonmoon.rpengine2.spigot.model.player.SpigotRpPlayerFactory;
import tv.twitch.moonmoon.rpengine2.spigot.nms.RpProtocolLib;
import tv.twitch.moonmoon.rpengine2.spigot.nms.RpProtocolLibModule;
import tv.twitch.moonmoon.rpengine2.spigot.nte.RpNametagEdit;
import tv.twitch.moonmoon.rpengine2.spigot.nte.RpNametagEditModule;
import tv.twitch.moonmoon.rpengine2.spigot.util.SpigotAsyncExecutor;
import tv.twitch.moonmoon.rpengine2.spigot.util.SpigotPluginOut;
import tv.twitch.moonmoon.rpengine2.util.AsyncExecutor;
import tv.twitch.moonmoon.rpengine2.util.DbPath;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.PluginOut;

import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

public class SpigotModule extends PluginModule {

    private final JavaPlugin plugin;

    public SpigotModule(JavaPlugin plugin) {
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
        super.configure();

        OptionalBinder.newOptionalBinder(binder(), RpProtocolLib.class);
        OptionalBinder.newOptionalBinder(binder(), RpNametagEdit.class);

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

    @Override
    protected void bindPlayerRepo(AnnotatedBindingBuilder<RpPlayerRepo> b) {
        b.to(SpigotRpPlayerRepo.class);
    }

    @Override
    protected void bindPlayerFactory(AnnotatedBindingBuilder<RpPlayerFactory> b) {
        b.to(SpigotRpPlayerFactory.class);
    }

    @Override
    protected void bindOptionFactory(AnnotatedBindingBuilder<OptionFactory> b) {
        b.to(SpigotOptionFactory.class);
    }

    @Override
    protected void bindAsyncExecutor(AnnotatedBindingBuilder<AsyncExecutor> b) {
        b.to(SpigotAsyncExecutor.class);
    }

    @Override
    protected void bindPluginOut(AnnotatedBindingBuilder<PluginOut> b) {
        b.to(SpigotPluginOut.class);
    }

    @Override
    protected ChatModule createChatModule() {
        return new SpigotChatModule();
    }

    @Override
    protected DuelsModule createDuelsModule() {
        return new SpigotDuelsModule();
    }

    @Override
    protected CombatLogModule createCombatLogModule() {
        return new SpigotCombatLogModule(plugin);
    }

    @Override
    protected boolean isChatEnabled() {
        return plugin.getConfig().getBoolean("chat.enabled");
    }

    @Override
    protected boolean isDuelsEnabled() {
        return plugin.getConfig().getBoolean("duels.enabled");
    }

    @Override
    protected boolean isCombatLogEnabled() {
        return plugin.getConfig().getBoolean("combatlog.enabled");
    }
}
