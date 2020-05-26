package tv.twitch.moonmoon.rpengine2.spigot;

import com.google.inject.Guice;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.Engine;

import java.util.Optional;

public final class RpEngine2 extends JavaPlugin {

    private SpigotEngine engine;

    @Override
    public void onEnable() {
        engine = Guice.createInjector(new SpigotModule(this))
            .getInstance(SpigotEngine.class);
        engine.init();
    }

    @Override
    public void onDisable() {
        if (engine != null) {
            engine.shutdown();
        }
    }

    /**
     * Returns the {@link SpigotEngine} instance of the plugin if the plugin is enabled, otherwise
     * returns empty.
     *
     * @return Engine instance
     */
    public static Optional<Engine> getEngine() {
        return Optional.ofNullable(getPlugin(RpEngine2.class).engine);
    }
}
