package tv.twitch.moonmoon.rpengine2.sponge.duel;

import tv.twitch.moonmoon.rpengine2.Config;
import tv.twitch.moonmoon.rpengine2.countdown.CountdownFactory;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.DuelMessenger;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.duel.impl.DefaultDuels;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.task.TaskFactory;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.Result;

public class SpongeDuels extends DefaultDuels {

    protected SpongeDuels(
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo,
        DuelInvites invites,
        RpEngine2 plugin,
        Config config,
        DuelerFactory duelerFactory,
        CountdownFactory countdownFactory,
        DuelMessenger duelMessenger,
        Messenger messenger,
        TaskFactory taskFactory
    ) {
        super(
            configRepo, playerRepo, invites, duelerFactory, countdownFactory, duelMessenger,
            messenger, config, taskFactory
        );
    }

    @Override
    public Result<Void> onStarted() {
        // TODO
        return Result.ok(null);
    }
}
