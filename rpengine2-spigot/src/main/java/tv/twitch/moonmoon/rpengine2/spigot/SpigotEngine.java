package tv.twitch.moonmoon.rpengine2.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.AbstractEngine;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.data.Defaults;
import tv.twitch.moonmoon.rpengine2.data.Migrations;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.CoreCommands;
import tv.twitch.moonmoon.rpengine2.spigot.nms.RpProtocolLib;
import tv.twitch.moonmoon.rpengine2.spigot.nte.RpNametagEdit;
import tv.twitch.moonmoon.rpengine2.spigot.util.ModuleLoader;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class SpigotEngine extends AbstractEngine implements ModuleLoader {

    private final JavaPlugin plugin;
    private final CoreListener listener;
    private final CoreCommands commands;
    private final RpProtocolLib protocol;
    private final RpNametagEdit nte;
    private final Logger log;

    @Inject
    public SpigotEngine(
        JavaPlugin plugin,
        RpDb db,
        Migrations migrations,
        Defaults defaults,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        CoreListener listener,
        CoreCommands commands,
        Optional<Chat> chat,
        Optional<Duels> duels,
        Optional<RpProtocolLib> protocol,
        Optional<RpNametagEdit> nte,
        Optional<CombatLog> combatLog
    ) {
        super(
            db, migrations, defaults, playerRepo, attributeRepo, selectRepo, chat, duels,
            combatLog
        );

        this.plugin = Objects.requireNonNull(plugin);
        this.listener = Objects.requireNonNull(listener);
        this.commands = Objects.requireNonNull(commands);
        this.protocol = protocol.orElse(null);
        this.nte = nte.orElse(null);
        log = plugin.getLogger();
    }

    void init() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        commands.register();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(listener, plugin);

        log.info("Connecting to database");

        if (!requireOk(initDb())) {
            return;
        }

        if (!requireOk(initModules())) {
            return;
        }

        if (protocol != null) {
            protocol.init();
        }

        if (nte != null) {
            nte.init();
        }

        log.info("Done");
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the {@link RpProtocolLib} module if enabled, empty otherwise
     *
     * @return Protocol lib module
     */
    public Optional<RpProtocolLib> getProtocolLibModule() {
        return Optional.ofNullable(protocol);
    }

    /**
     * Returns the {@link RpNametagEdit} module if enabled, empty otherwise
     *
     * @return Nametag module
     */
    public Optional<RpNametagEdit> getNametagEditModule() {
        return Optional.ofNullable(nte);
    }
}
