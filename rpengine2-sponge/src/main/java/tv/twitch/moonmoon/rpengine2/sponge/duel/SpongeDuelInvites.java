package tv.twitch.moonmoon.rpengine2.sponge.duel;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.impl.AbstractDuelInvites;
import tv.twitch.moonmoon.rpengine2.sponge.Config;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.util.Messenger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Singleton
public class SpongeDuelInvites extends AbstractDuelInvites {

    private final RpEngine2 plugin;
    private final Config config;
    private SpongeExecutorService task;

    @Inject
    public SpongeDuelInvites(
        RpEngine2 plugin,
        Config config,
        Messenger messenger,
        RpPlayerRepo playerRepo
    ) {
        super(messenger, playerRepo);
        this.plugin = Objects.requireNonNull(plugin);
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public void startWatching() {
        int inviteExpireSecs = config.getRoot()
            .getNode("duels", "inviteExpireSecs")
            .getInt(300);

        task = Sponge.getScheduler().createAsyncExecutor(plugin);
        task.scheduleAtFixedRate(() ->
            pruneExpiredInvites(inviteExpireSecs),
            0, 500, TimeUnit.MILLISECONDS
        );
    }

    @Override
    protected void finalize() {
        if (task != null && !task.isShutdown()) {
            task.shutdown();
        }
    }
}
