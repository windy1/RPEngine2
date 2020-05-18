package tv.twitch.moonmoon.rpengine2.data;

import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.*;
import java.util.logging.Logger;

public class DataManager {

    private final RpDb db;
    private final Migrations migrations;
    private final Defaults defaults;
    private final List<Repo> repos;
    private final Logger log;

    @Inject
    public DataManager(
        RpDb db,
        Migrations migrations,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        Defaults defaults,
        @PluginLogger Logger log
    ) {
        this.db = Objects.requireNonNull(db);
        this.migrations = Objects.requireNonNull(migrations);
        this.defaults = Objects.requireNonNull(defaults);
        this.log = Objects.requireNonNull(log);
        repos = new ArrayList<>(Arrays.asList(playerRepo, attributeRepo, selectRepo));
    }

    public Result<Void> init() {
        log.info("Connecting to database");

        Optional<String> err = db.connect().getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        log.info("Running migrations");

        err = migrations.migrate().getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        log.info("Loading data");

        for (Repo repo : repos) {
            err = repo.load().getError();
            if (err.isPresent()) {
                return Result.error(err.get());
            }
        }

        defaults.saveDefaults();

        return Result.ok(null);
    }
}
