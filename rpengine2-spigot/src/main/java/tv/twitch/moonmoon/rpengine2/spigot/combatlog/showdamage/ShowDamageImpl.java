package tv.twitch.moonmoon.rpengine2.spigot.combatlog.showdamage;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.HologramPlugin;
import com.sainttx.holograms.api.line.TextLine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class ShowDamageImpl implements ShowDamage {

    public static HologramManager hologramManager = null;

    private final Map<Integer, DamageHologram> holograms = new HashMap<>();
    private final Plugin plugin;
    private final ShowDamageListener listener;
    private final Logger log;

    private BukkitTask hologramWatcher;

    @Inject
    public ShowDamageImpl(Plugin plugin, ShowDamageListener listener) {
        this.plugin = Objects.requireNonNull(plugin);
        this.listener = Objects.requireNonNull(listener);
        log = plugin.getLogger();
    }

    @Override
    public DamageHologram getHologram(Entity entity) {
        Objects.requireNonNull(entity);
        int entityId = entity.getEntityId();
        Location location = entity.getLocation().add(0, 2, 0);
        return holograms.computeIfAbsent(entityId, k -> new DamageHologram(new Hologram(
            Integer.toString(entityId),
            location
        )));
    }

    @Override
    public void pushDamage(Entity entity, double value) {
        updateHologram(entity, value, false);
    }

    @Override
    public void pushRegen(Entity entity, double value) {
        updateHologram(entity, value, true);
    }

    private void updateHologram(Entity entity, double value, boolean regen) {
        HologramManager hologramManager = getHologramManager().orElse(null);
        if (hologramManager == null) {
            return;
        }

        Hologram hologram = getHologram(entity).getHologram();
        Location newLocation = entity.getLocation().add(0, 2, 0);
        FileConfiguration config = plugin.getConfig();

        hologram.despawn();

        if (!hologram.getLocation().equals(newLocation)) {
            hologram.teleport(newLocation);
        }

        if (hologram.getLines().size() > 0) {
            hologram.removeLine(hologram.getLine(0));
        }

        String fmt = config.getString("combatlog.showdamage.numberFormat", "%.1f");
        if (fmt == null) {
            log.warning("invalid format string for combatlog.showdamage");
            fmt = "%.1f";
        }

        String str;
        try {
            str = String.format(fmt, value);
        } catch (IllegalArgumentException ex) {
            log.warning(String.format(
                "invalid format string for combatlog.showdamage: `%s`", ex.getMessage())
            );
            str = String.format("%.1f", value);
        }

        hologram.addLine(new TextLine(
            hologram,
            (regen ? ChatColor.DARK_GREEN + "+" : ChatColor.DARK_RED + "-") + str)
        );

        hologram.spawn();
    }

    @Override
    public Result<Void> init() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        startWatching();

        return Result.ok(null);
    }

    public static Optional<HologramManager> getHologramManager() {
        if (hologramManager != null) {
            return Optional.of(hologramManager);
        }

        return Optional.ofNullable(JavaPlugin.getPlugin(HologramPlugin.class)
            .getHologramManager()
        );
    }

    private void startWatching() {
        FileConfiguration config = plugin.getConfig();
        double staySecs = config.getDouble("combatlog.showdamage.staySecs", 0.5);

        hologramWatcher = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Iterator<Map.Entry<Integer, DamageHologram>> it = holograms.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<Integer, DamageHologram> entry = it.next();
                DamageHologram hologram = entry.getValue();
                Instant spawnTime = hologram.getSpawnTime();
                Instant now = Instant.now();
                double elapsedSecs = Duration.between(spawnTime, now).toMillis() / 1000.0;

                if (elapsedSecs > staySecs) {
                    hologram.getHologram().despawn();
                    it.remove();
                }
            }
        }, 0, config.getInt("combatlog.showdamage.tickThrottle", 10));
    }

    @Override
    protected void finalize() {
        if (hologramWatcher != null && !hologramWatcher.isCancelled()) {
            hologramWatcher.cancel();
        }
    }
}
