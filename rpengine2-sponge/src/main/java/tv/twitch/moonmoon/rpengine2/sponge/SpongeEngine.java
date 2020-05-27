package tv.twitch.moonmoon.rpengine2.sponge;

import org.slf4j.Logger;
import tv.twitch.moonmoon.rpengine2.AbstractEngine;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.data.Migrations;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.data.Defaults;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class SpongeEngine extends AbstractEngine {

    private final Config config;
    private final Logger log;

    @Inject
    public SpongeEngine(
        Config config,
        @PluginLogger Logger log,
        RpDb db,
        Migrations migrations,
        Defaults defaults,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        Optional<Chat> chat,
        Optional<Duels> duels,
        Optional<CombatLog> combatLog
    ) {
        super(
            db, migrations, defaults, playerRepo, attributeRepo, selectRepo, chat, duels,
            combatLog
        );

        this.config = Objects.requireNonNull(config);
        this.log = Objects.requireNonNull(log);
    }

    void init() {
        log.info("Connecting to database");

        if (!requireOk(initDb())) {
            return;
        }

        if (!requireOk(initModules())) {
            return;
        }

        log.info("Done");
    }

    private <T> boolean requireOk(Result<T> r) {
        Optional<String> err = r.getError();
        if (err.isPresent()) {
            log.warn(err.get());
            return false;
        }

        return true;
    }
}
