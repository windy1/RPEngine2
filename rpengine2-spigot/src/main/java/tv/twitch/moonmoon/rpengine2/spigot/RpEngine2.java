package tv.twitch.moonmoon.rpengine2.spigot;

import com.google.inject.Guice;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.Engine;

import java.util.Optional;
import java.util.logging.Logger;

public final class RpEngine2 extends JavaPlugin {

    private Engine engine;

    @Override
    public void onEnable() {
        engine = Guice.createInjector(new SpigotModule(this))
            .getInstance(Engine.class);
        engine.start();
    }

    @Override
    public void onDisable() {
        if (engine != null) {
            Logger log = getLogger();

            log.info("Shutting down");
            engine.shutdown();
            log.info("Done");
        }
    }

    /**
     * Returns the {@link Engine} instance of the plugin if the plugin is enabled, otherwise
     * returns empty.
     *
     * @return Engine instance
     */
    public static Optional<Engine> getEngine() {
        return Optional.ofNullable(getPlugin(RpEngine2.class).engine);
    }
}
