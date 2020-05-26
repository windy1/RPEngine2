package tv.twitch.moonmoon.rpengine2.spigot.combatlog.showdamage;

import com.sainttx.holograms.api.HologramPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.Objects;

public class ShowDamageListener implements Listener {

    private final Plugin plugin;
    private final ShowDamage showDamage;

    @Inject
    public ShowDamageListener(Plugin plugin, ShowDamage showDamage) {
        this.plugin = Objects.requireNonNull(plugin);
        this.showDamage = Objects.requireNonNull(showDamage);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Entity entity = e.getEntity();
        FileConfiguration config = plugin.getConfig();

        if ((config.getBoolean("combatlog.showdamage.playersOnly")
                && !(entity instanceof Player))
                || !config.getBoolean("combatlog.showdamage.damage")) {
            return;
        }

        showDamage.pushDamage(entity, e.getFinalDamage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("combatlog.showdamage.regen")) {
            return;
        }

        showDamage.pushRegen(entity, e.getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent e) {
        Plugin plugin = e.getPlugin();
        if (plugin instanceof HologramPlugin) {
            ShowDamageImpl.hologramManager = ((HologramPlugin) plugin).getHologramManager();
        }
    }
}
