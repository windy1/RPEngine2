package tv.twitch.moonmoon.rpengine2;

import com.google.inject.Guice;
import org.bukkit.plugin.java.JavaPlugin;

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

    public Engine getEngine() {
        return engine;
    }
}
