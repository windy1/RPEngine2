package tv.twitch.moonmoon.rpengine2.data;

import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.di.PluginLogger;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class DataManager {

    private final RpDb db;
    private final RpPlayerRepo playerRepo;
    private final AttributeRepo attributeRepo;
    private final Logger log;

    @Inject
    public DataManager(
        RpDb db,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        @PluginLogger Logger log
    ) {
        this.db = Objects.requireNonNull(db);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.log = Objects.requireNonNull(log);
    }

    public void init() {
        db.connectAsync(r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
                // TODO: must do this on main thread
//                pluginManager.disablePlugin(plugin);
            } else {
                this.onDbConnect();
            }
        });
    }

    private void onDbConnect() {
        log.info("Connected to database");
        playerRepo.load();
        attributeRepo.load();
    }
}
