package tv.twitch.moonmoon.rpengine2.data;

import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DataManager {

    private final RpDb db;
    private final Defaults defaults;
    private final List<Repo> repos;

    @Inject
    public DataManager(
        RpDb db,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        Defaults defaults
    ) {
        this.db = Objects.requireNonNull(db);
        this.defaults = Objects.requireNonNull(defaults);
        repos = new ArrayList<>(Arrays.asList(playerRepo, attributeRepo, selectRepo));
    }

    public void init() {
        db.connect();
        repos.forEach(Repo::load);
        defaults.saveDefaults();
    }
}
