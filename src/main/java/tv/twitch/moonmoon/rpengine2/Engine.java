package tv.twitch.moonmoon.rpengine2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.CoreCommands;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.data.DataManager;
import tv.twitch.moonmoon.rpengine2.data.DataManagerImpl;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.nms.RpProtocolLib;
import tv.twitch.moonmoon.rpengine2.nte.RpNametagEdit;
import tv.twitch.moonmoon.rpengine2.util.ModuleLoader;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public class Engine implements ModuleLoader {

    private final JavaPlugin plugin;
    private final CoreListener listener;
    private final CoreCommands commands;
    private final DataManagerImpl dataManager;
    private final Chat chat;
    private final Duels duels;
    private final RpProtocolLib protocol;
    private final RpNametagEdit nte;
    private final CombatLog combatLog;
    private final Logger log;

    @Inject
    public Engine(
        JavaPlugin plugin,
        CoreListener listener,
        CoreCommands commands,
        DataManagerImpl dataManager,
        Optional<Chat> chat,
        Optional<Duels> duels,
        Optional<RpProtocolLib> protocol,
        Optional<RpNametagEdit> nte,
        Optional<CombatLog> combatLog
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.listener = Objects.requireNonNull(listener);
        this.commands = Objects.requireNonNull(commands);
        this.dataManager = Objects.requireNonNull(dataManager);
        this.chat = chat.orElse(null);
        this.duels = duels.orElse(null);
        this.protocol = protocol.orElse(null);
        this.combatLog = combatLog.orElse(null);
        this.nte = nte.orElse(null);
        log = plugin.getLogger();
    }

    void init() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        commands.register();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(listener, plugin);

        if (dataManager.init().getError().isPresent()) {
            pluginManager.disablePlugin(plugin);
            return;
        }

        if (chat != null && !requireOk(chat.init())) {
            return;
        }

        if (duels != null && !requireOk(duels.init())) {
            return;
        }

        if (combatLog != null && !requireOk(combatLog.init())) {
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

    void shutdown() {
        log.info("Shutting down");

        dataManager.shutdown();

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
     * Returns the {@link DataManagerImpl} instance, responsible for managing the plugins
     * internal data
     *
     * @return DataManager instance
     */
    public DataManager getDataManager() {
        return dataManager;
    }

    /**
     * Returns the {@link Chat} module instance if enabled, empty otherwise
     *
     * @return Chat module
     */
    public Optional<Chat> getChatModule() {
        return Optional.ofNullable(chat);
    }

    /**
     * Returns the {@link Duels} module instance if enabled, empty otherwise
     *
     * @return Duels module
     */
    public Optional<Duels> getDuelsModule() {
        return Optional.ofNullable(duels);
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

    /**
     * Returns the {@link CombatLog} module instance if enabled, empty otherwise
     *
     * @return CombatLog module
     */
    public Optional<CombatLog> getCombatLogModule() {
        return Optional.ofNullable(combatLog);
    }
}
