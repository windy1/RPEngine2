package tv.twitch.moonmoon.rpengine2.spigot.combatlog;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.multibindings.OptionalBinder;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLogModule;
import tv.twitch.moonmoon.rpengine2.spigot.combatlog.showdamage.ShowDamage;
import tv.twitch.moonmoon.rpengine2.spigot.combatlog.showdamage.ShowDamageModule;

import java.util.Objects;
import java.util.logging.Logger;

public class SpigotCombatLogModule extends CombatLogModule {

    private final Plugin plugin;
    private final Logger log;

    public SpigotCombatLogModule(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        log = plugin.getLogger();
    }

    @Override
    protected void configure() {
        super.configure();

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

    @Override
    protected void bindCombatLog(AnnotatedBindingBuilder<CombatLog> b) {
        b.to(SpigotCombatLog.class);
    }
}
