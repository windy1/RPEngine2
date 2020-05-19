package tv.twitch.moonmoon.rpengine2;

import com.google.inject.Guice;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class RpEngine2 extends JavaPlugin {

    private Engine engine;

    @Override
    public void onEnable() {
        engine = Guice.createInjector(new CoreModule(this)).getInstance(Engine.class);
        engine.init();
    }

    @Override
    public void onDisable() {
        if (engine != null) {
            engine.shutdown();
        }
    }

    /**
     * Returns the {@link Engine} instance of the plugin if the plugin is enabled, otherwise
     * returns empty.
     *
     * @return Engine instance
     */
    public static Optional<Engine> getEngine() {
        return Optional.ofNullable(JavaPlugin.getPlugin(RpEngine2.class).engine);
    }
}
