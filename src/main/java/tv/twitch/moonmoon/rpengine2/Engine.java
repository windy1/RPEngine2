package tv.twitch.moonmoon.rpengine2;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.CoreCommands;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.data.DataManager;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.nms.ProtocolLibPlugin;
import tv.twitch.moonmoon.rpengine2.nte.NametagEditPlugin;
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
    private final DataManager dataManager;
    private final Chat chat;
    private final Duels duels;
    private final ProtocolLibPlugin protocol;
    private final NametagEditPlugin nte;
    private final CombatLog combatLog;
    private final Logger log;

    @Inject
    public Engine(
        JavaPlugin plugin,
        CoreListener listener,
        CoreCommands commands,
        DataManager dataManager,
        Optional<Chat> chat,
        Optional<Duels> duels,
        Optional<ProtocolLibPlugin> protocol,
        Optional<NametagEditPlugin> nte,
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

    public void init() {
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

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
