package tv.twitch.moonmoon.rpengine2;

import com.google.inject.Guice;
import org.bukkit.plugin.java.JavaPlugin;

public final class RpEngine2 extends JavaPlugin {

    @Override
    public void onEnable() {
        Guice.createInjector(new CoreModule(this))
            .getInstance(Engine.class)
            .init();
    }

    @Override
    public void onDisable() {
    }
}
