package tv.twitch.moonmoon.rpengine2.spigot.duel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import tv.twitch.moonmoon.rpengine2.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.AbstractDuels;
import tv.twitch.moonmoon.rpengine2.duel.Duel;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.Dueler;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.data.player.SpigotRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.spigot.duel.cmd.DuelCommands;
import tv.twitch.moonmoon.rpengine2.spigot.model.player.SpigotRpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.util.Countdown;
import tv.twitch.moonmoon.rpengine2.util.Lang;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class SpigotDuels extends AbstractDuels {

    private final Plugin plugin;
    private final DuelCommands commands;
    private final DuelListener listener;
    private final Logger log;

    private BukkitTask duelWatcher;

    @Inject
    public SpigotDuels(
        Plugin plugin,
        DuelCommands commands,
        DuelListener listener,
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo,
        DuelInvites invites
    ) {
        super(configRepo, playerRepo, invites);
        this.plugin = Objects.requireNonNull(plugin);
        this.commands = Objects.requireNonNull(commands);
        this.listener = Objects.requireNonNull(listener);
        log = plugin.getLogger();
    }

    @Override
    protected void startCountdown(Set<UUID> playerIds, Runnable onComplete) {
        Countdown.from(plugin.getConfig(), playerIds, 3, onComplete).start();
    }

    @Override
    protected Dueler createDueler(RpPlayer p) {
        return new SpigotDueler(p);
    }

    @Override
    protected void onDuelEnd(Dueler winner, Dueler loser) {
        SpigotRpPlayerRepo players = (SpigotRpPlayerRepo) playerRepo;
        String message =
            players.getIdentity(winner.getPlayer()) +
                ChatColor.GOLD +
                Lang.getString("duels.duelEnd1") +
                players.getIdentity(loser.getPlayer()) +
                ChatColor.GOLD +
                Lang.getString("duels.duelEnd2");

        Bukkit.broadcastMessage(message);
    }

    @Override
    protected void onDuelTimeout(Duel duel) {
        SpigotRpPlayerRepo players = (SpigotRpPlayerRepo) playerRepo;
        String message =
            ChatColor.GOLD +
                Lang.getString("duels.duelTimeout1") +
                players.getIdentity(duel.getPlayer1().getPlayer()) +
                ChatColor.GOLD +
                Lang.getString("duels.duelTimeout2") +
                players.getIdentity(duel.getPlayer2().getPlayer()) +
                ChatColor.GOLD +
                Lang.getString("duels.duelTimeout3");

        Bukkit.broadcastMessage(message);
    }

    @Override
    public void forfeitDuel(RpPlayer player) {
        Objects.requireNonNull(player);
        Duel duel = getActiveDuel(player.getUUID()).orElse(null);
        Player mcPlayer = ((SpigotRpPlayer) player).getPlayer().orElse(null);

        if (mcPlayer == null) {
            return;
        }

        if (duel == null) {
            mcPlayer.sendMessage(ChatColor.RED + Lang.getString("duels.noActiveDuel"));
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

        SpigotRpPlayerRepo players = (SpigotRpPlayerRepo) playerRepo;

        String message =
            players.getIdentity(player) +
                ChatColor.GOLD +
                Lang.getString("duels.forfeit1") +
                players.getIdentity(winner) +
                ChatColor.GOLD +
                Lang.getString("duels.forfeit2");

        Bukkit.broadcastMessage(message);
    }

    @Override
    public void handlePlayerJoined(RpPlayer player) {
        configRepo.getConfig(player).getError().ifPresent(log::warning);
    }

    @Override
    public Result<Void> init() {
        commands.register();
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        invites.startWatching();
        startWatchingDuels();

        return configRepo.load().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> Result.ok(null));
    }

    private void startWatchingDuels() {
        int maxSecs = plugin.getConfig().getInt("duels.maxSecs", 300);
        duelWatcher = Bukkit.getScheduler().runTaskTimer(plugin, () ->
            cleanDuels(maxSecs),
            0, 10
        );
    }

    @Override
    protected void finalize() {
        if (duelWatcher != null && !duelWatcher.isCancelled()) {
            duelWatcher.cancel();
        }
    }
}
