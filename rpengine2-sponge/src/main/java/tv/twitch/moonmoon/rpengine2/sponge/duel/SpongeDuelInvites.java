package tv.twitch.moonmoon.rpengine2.sponge.duel;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import tv.twitch.moonmoon.rpengine2.duel.AbstractDuelInvites;
import tv.twitch.moonmoon.rpengine2.sponge.Config;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.util.Lang;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class SpongeDuelInvites extends AbstractDuelInvites {

    private final RpEngine2 plugin;
    private final Config config;
    private SpongeExecutorService task;

    @Inject
    public SpongeDuelInvites(RpEngine2 plugin, Config config) {
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
    protected void onInviteExpired(UUID playerId, UUID targetId) {
        Player player = Sponge.getServer().getPlayer(playerId).orElse(null);
        Player target = Sponge.getServer().getPlayer(targetId).orElse(null);
        if (player != null && target != null && player.isOnline()) {
            player.sendMessage(Text.of(
                TextColors.RED, Lang.getString("duels.inviteExpired", target.getName())
            ));
        }
    }

    @Override
    protected void finalize() {
        if (task != null && !task.isShutdown()) {
            task.shutdown();
        }
    }
}
