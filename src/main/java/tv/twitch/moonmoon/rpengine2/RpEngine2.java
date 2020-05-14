package tv.twitch.moonmoon.rpengine2;

import com.google.inject.Guice;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.di.RpModule;

public final class RpEngine2 extends JavaPlugin {

    @Override
    public void onEnable() {
        Guice.createInjector(new RpModule(this))
            .getInstance(Bootstrap.class)
            .init();
    }

    @Override
    public void onDisable() {
    }
}
