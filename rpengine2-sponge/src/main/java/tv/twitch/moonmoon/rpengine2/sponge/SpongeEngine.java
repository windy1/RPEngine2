package tv.twitch.moonmoon.rpengine2.sponge;

import tv.twitch.moonmoon.rpengine2.impl.DefaultEngine;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.data.Defaults;
import tv.twitch.moonmoon.rpengine2.data.Migrations;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.util.Messenger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class SpongeEngine extends DefaultEngine {

    @Inject
    public SpongeEngine(
        RpDb db,
        Migrations migrations,
        Defaults defaults,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        Optional<Chat> chat,
        Optional<Duels> duels,
        Optional<CombatLog> combatLog,
        Messenger messenger
    ) {
        super(
            db, migrations, defaults, playerRepo, attributeRepo, selectRepo, chat, duels,
            combatLog, messenger
        );
    }

    @Override
    protected void onFailure() {
    }

    @Override
    protected boolean onStarted() {
        return true;
    }
}
