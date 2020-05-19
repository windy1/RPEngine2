package tv.twitch.moonmoon.rpengine2.combatlog;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.OptionalBinder;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.combatlog.showdamage.ShowDamage;
import tv.twitch.moonmoon.rpengine2.combatlog.showdamage.ShowDamageModule;

import java.util.Objects;
import java.util.logging.Logger;

public class CombatLogModule extends AbstractModule {

    private final Plugin plugin;
    private final Logger log;

    public CombatLogModule(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        log = plugin.getLogger();
    }

    @Override
    protected void configure() {
        bind(CombatLog.class).to(CombatLogImpl.class);

        boolean showDamageEnabled =
            plugin.getConfig().getBoolean("combatlog.showdamage.enabled");

        OptionalBinder.newOptionalBinder(binder(), ShowDamage.class);

        if (showDamageEnabled) {
            try {
                Class.forName("com.sainttx.holograms.HologramPlugin");
                install(new ShowDamageModule());
            } catch (ClassNotFoundException e) {
                log.warning(
                    "combatlog.showdamage was enabled but is missing required " +
                        "dependency Holograms"
                );
            }
        }
    }
}
