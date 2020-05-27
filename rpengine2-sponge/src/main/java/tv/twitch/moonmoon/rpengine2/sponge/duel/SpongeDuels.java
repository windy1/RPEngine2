package tv.twitch.moonmoon.rpengine2.sponge.duel;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import tv.twitch.moonmoon.rpengine2.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.AbstractDuels;
import tv.twitch.moonmoon.rpengine2.duel.Duel;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.Dueler;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.sponge.Config;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.sponge.data.player.SpongeRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.sponge.model.player.SpongeRpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Lang;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpongeDuels extends AbstractDuels {

    private final RpEngine2 plugin;
    private final Logger log;
    private final Config config;

    private SpongeExecutorService duelWatcher;

    protected SpongeDuels(
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo,
        DuelInvites invites,
        RpEngine2 plugin,
        @PluginLogger Logger log,
        Config config
    ) {
        super(configRepo, playerRepo, invites);
        this.plugin = Objects.requireNonNull(plugin);
        this.log = Objects.requireNonNull(log);
        this.config = Objects.requireNonNull(config);
    }

    @Override
    protected void startCountdown(Set<UUID> playerIds, Runnable onComplete) {
        // TODO
    }

    @Override
    protected Dueler createDueler(RpPlayer p) {
        return new SpongeDueler(p);
    }

    @Override
    protected void onDuelEnd(Dueler winner, Dueler loser) {
        SpongeRpPlayerRepo players = (SpongeRpPlayerRepo) playerRepo;
        Text message = Text.of(
            players.getIdentity(winner.getPlayer()),
            TextColors.GOLD,
            Lang.getString("duels.duelEnd1"),
            players.getIdentity(loser.getPlayer()),
            TextColors.GOLD,
            Lang.getString("duels.duelEnd2")
        );

        Sponge.getServer().getBroadcastChannel().send(message);
    }

    @Override
    protected void onDuelTimeout(Duel duel) {
        Text message = Text.of(
            TextColors.GOLD,
            Lang.getString("duels.duelTimeout1"),
            TextColors.GOLD,
            Lang.getString("duels.duelTimeout2"),
            ((SpongeRpPlayerRepo) playerRepo).getIdentity(duel.getPlayer2().getPlayer()),
            TextColors.GOLD,
            Lang.getString("duels.duelTimeout3")
        );

        Sponge.getServer().getBroadcastChannel().send(message);
    }

    @Override
    public void forfeitDuel(RpPlayer player) {
        Objects.requireNonNull(player);
        Duel duel = getActiveDuel(player.getUUID()).orElse(null);
        Player mcPlayer = ((SpongeRpPlayer) player).getPlayer().orElse(null);

        if (mcPlayer == null) {
            return;
        }

        if (duel == null) {
            mcPlayer.sendMessage(Text.of(TextColors.RED, Lang.getString("duels.noActiveDuel")));
            return;
        }

        duel.getPlayer1().resetPlayer();
        duel.getPlayer2().resetPlayer();
        activeDuels.remove(duel);

        RpPlayer winner;
        if (duel.getPlayer1().getPlayer().equals(player)) {
            winner = duel.getPlayer2().getPlayer();
        } else {
            winner = duel.getPlayer1().getPlayer();
        }

        SpongeRpPlayerRepo players = (SpongeRpPlayerRepo) playerRepo;

        Text message = Text.of(
            players.getIdentity(player),
            TextColors.GOLD,
            Lang.getString("duels.forfeit1"),
            players.getIdentity(winner),
            TextColors.GOLD,
            Lang.getString("duels.forfeit2")
        );

        Sponge.getServer().getBroadcastChannel().send(message);
    }

    @Override
    public void handlePlayerJoined(RpPlayer player) {
        configRepo.getConfig(player).getError().ifPresent(log::warn);
    }

    @Override
    public Result<Void> init() {
        // TODO

        invites.startWatching();
        startWatchingDuels();

        return configRepo.load().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> Result.ok(null));
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
    protected void finalize() throws Throwable {
        if (duelWatcher != null && duelWatcher.isShutdown()) {
            duelWatcher.shutdown();
        }
    }
}
