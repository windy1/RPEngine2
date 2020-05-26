package tv.twitch.moonmoon.rpengine2.spigot;

import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.multibindings.OptionalBinder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.PluginModule;
import tv.twitch.moonmoon.rpengine2.chat.ChatModule;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLogModule;
import tv.twitch.moonmoon.rpengine2.data.Defaults;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeDbo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectDbo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.spigot.chat.SpigotChatModule;
import tv.twitch.moonmoon.rpengine2.spigot.combatlog.SpigotCombatLogModule;
import tv.twitch.moonmoon.rpengine2.spigot.data.SpigotDefaults;
import tv.twitch.moonmoon.rpengine2.spigot.data.attribute.SpigotAttributeDbo;
import tv.twitch.moonmoon.rpengine2.spigot.data.player.SpigotRpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.spigot.data.player.SpigotRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.spigot.data.select.SpigotSelectDbo;
import tv.twitch.moonmoon.rpengine2.spigot.data.select.SpigotSelectRepoImpl;
import tv.twitch.moonmoon.rpengine2.spigot.duel.SpigotDuelsModule;
import tv.twitch.moonmoon.rpengine2.spigot.nms.RpProtocolLib;
import tv.twitch.moonmoon.rpengine2.spigot.nms.RpProtocolLibModule;
import tv.twitch.moonmoon.rpengine2.spigot.nte.RpNametagEdit;
import tv.twitch.moonmoon.rpengine2.spigot.nte.RpNametagEditModule;
import tv.twitch.moonmoon.rpengine2.util.DbPath;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;

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
    protected void bindAttributeDbo(AnnotatedBindingBuilder<AttributeDbo> b) {
        b.to(SpigotAttributeDbo.class);
    }

    @Override
    protected void bindDefaults(AnnotatedBindingBuilder<Defaults> b) {
        b.to(SpigotDefaults.class);
    }

    @Override
    protected void bindPlayerRepo(AnnotatedBindingBuilder<RpPlayerRepo> b) {
        b.to(SpigotRpPlayerRepo.class);
    }

    @Override
    protected void bindPlayerDbo(AnnotatedBindingBuilder<RpPlayerDbo> b) {
        b.to(SpigotRpPlayerDbo.class);
    }

    @Override
    protected void bindSelectRepo(AnnotatedBindingBuilder<SelectRepo> b) {
        b.to(SpigotSelectRepoImpl.class);
    }

    @Override
    protected void bindSelectDbo(AnnotatedBindingBuilder<SelectDbo> b) {
        b.to(SpigotSelectDbo.class);
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
