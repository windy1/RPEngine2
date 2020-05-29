package tv.twitch.moonmoon.rpengine2.sponge.duel;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import tv.twitch.moonmoon.rpengine2.countdown.CountdownFactory;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.DuelMessenger;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.duel.impl.DefaultDuels;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.sponge.SpongeConfig;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SpongeDuels extends DefaultDuels {

    private final RpEngine2 plugin;
    private final SpongeConfig config;

    private SpongeExecutorService duelWatcher;

    protected SpongeDuels(
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo,
        DuelInvites invites,
        RpEngine2 plugin,
        SpongeConfig config,
        DuelerFactory duelerFactory,
        CountdownFactory countdownFactory,
        DuelMessenger duelMessenger,
        Messenger messenger
    ) {
        super(
            configRepo, playerRepo, invites, duelerFactory, countdownFactory, duelMessenger,
            messenger
        );
        this.plugin = Objects.requireNonNull(plugin);
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public Result<Void> onStarted() {
        // TODO
        return Result.ok(null);
    }

    private void startWatchingDuels() {
        int maxSecs = config.getRoot().getNode("duels", "maxSecs").getInt(300);
        duelWatcher = Sponge.getScheduler().createSyncExecutor(plugin);
        duelWatcher.scheduleAtFixedRate(() ->
            cleanDuels(maxSecs),
            0, 500, TimeUnit.MILLISECONDS
        );
    }

    @Override
    protected void finalize() {
        if (duelWatcher != null && duelWatcher.isShutdown()) {
            duelWatcher.shutdown();
        }
    }
}
