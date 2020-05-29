package tv.twitch.moonmoon.rpengine2.spigot;

import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.Config;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotConfig implements Config {

    private final Plugin plugin;

    @Inject
    public SpigotConfig(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return plugin.getConfig().getBoolean(path, def);
    }

    @Override
    public int getInt(String path, int def) {
        return plugin.getConfig().getInt(path, def);
    }

    @Override
    public String getString(String path, String def) {
        return plugin.getConfig().getString(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        return plugin.getConfig().getDouble(path, def);
    }
}
