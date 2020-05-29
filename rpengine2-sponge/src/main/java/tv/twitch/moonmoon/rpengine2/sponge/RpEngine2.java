package tv.twitch.moonmoon.rpengine2.sponge;

import com.google.inject.Guice;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import tv.twitch.moonmoon.rpengine2.Engine;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(id = "rpengine2", name = "RPEngine2", version = "2.0", description = "RPEngine rewrite")
public class RpEngine2 {

    @Inject
    private Logger log;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path configPath;

    private Engine engine;

    @Listener
    public void onServerStart(GameStartedServerEvent e) {
        SpongeConfig config = loadConfig().orElse(null);
        if (config == null) {
            return;
        }

        Path dbPath = configPath.getParent().resolve("plugin.db");
        engine = Guice.createInjector(new SpongeModule(this, dbPath, config, log))
            .getInstance(Engine.class);
        engine.start();
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent e) {
        if (engine != null) {
            log.info("Shutting down");
            engine.shutdown();
            log.info("Done");
        }
    }

    private Optional<SpongeConfig> loadConfig() {
        try {
            return Optional.of(SpongeConfig.load(configPath));
        } catch (IOException e) {
            String message = "error loading config: `%s`";
            log.warn(String.format(message, e.getMessage()));
            return Optional.empty();
        }
    }

    /**
     * Returns the {@link Engine} instance of the plugin if the plugin is enabled, otherwise
     * returns empty.
     *
     * @return Engine instance
     */
    public static Optional<Engine> getEngine() {
        return Sponge.getPluginManager().getPlugin("rpengine2")
            .flatMap(PluginContainer::getInstance)
            .map(p -> (RpEngine2) p)
            .map(p -> p.engine);
    }
}
