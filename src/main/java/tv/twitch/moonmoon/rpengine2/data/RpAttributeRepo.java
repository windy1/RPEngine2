package tv.twitch.moonmoon.rpengine2.data;

import tv.twitch.moonmoon.rpengine2.di.PluginLogger;
import tv.twitch.moonmoon.rpengine2.model.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Singleton
public class RpAttributeRepo {

    private final RpDb db;
    private final RpPlayerRepo playerRepo;
    private final Logger log;

    @Inject
    public RpAttributeRepo(RpDb db, RpPlayerRepo playerRepo, @PluginLogger Logger log) {
        this.db = Objects.requireNonNull(db);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.log = Objects.requireNonNull(log);
    }

    public void createAttributeAsync(
        String name,
        AttributeType type,
        String display,
        String defaultValue,
        Consumer<Result<Void>> callback
    ) {
        db.insertAttributeAsync(name, display, type.getId(), defaultValue, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                // insertion failed
                log.warning(err.get());
                callback.accept(Result.error(StringUtils.GENERIC_ERROR));
                return;
            }

            long attributeId = r.get();
            if (attributeId == -1) {
                // attribute exists already
                callback.accept(Result.error("Attribute already exists"));
                return;
            }

            // add new attribute to each player
            playerRepo.flushJoinedPlayers();

            for (RpPlayer player : playerRepo.getPlayers()) {
                playerRepo.setAttributeAsync(player, (int) attributeId, null, s ->
                    s.getError().ifPresent(log::warning)
                );
            }

            callback.accept(Result.ok(null));
        });
    }
}
