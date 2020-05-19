package tv.twitch.moonmoon.rpengine2.duel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.util.Countdown;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.cmd.DuelCommands;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.*;
import java.util.logging.Logger;

public class DuelsImpl implements Duels {

    private final Plugin plugin;
    private final DuelCommands commands;
    private final DuelListener listener;
    private final DuelConfigRepo configRepo;
    private final RpPlayerRepo playerRepo;
    private final DuelInvites invites;
    private final Set<Duel> activeDuels = Collections.synchronizedSet(new HashSet<>());
    private final Logger log;

    @Inject
    public DuelsImpl(
        Plugin plugin,
        DuelCommands commands,
        DuelListener listener,
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo,
        DuelInvites invites
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.commands = Objects.requireNonNull(commands);
        this.listener = Objects.requireNonNull(listener);
        this.configRepo = Objects.requireNonNull(configRepo);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.invites = Objects.requireNonNull(invites);
        log = plugin.getLogger();
    }

    @Override
    public void startDuel(RpPlayer p1, RpPlayer p2) {
        Set<UUID> playerIds = new HashSet<>(Arrays.asList(p1.getUUID(), p2.getUUID()));
        Duel duel = new Duel(new Dueler(p1), new Dueler(p2));

        activeDuels.add(duel);

        Countdown.from(plugin.getConfig(), playerIds, 3, () ->
            duel.setStarted(true)
        ).start();
    }

    @Override
    public void endDuel(Duel duel, Dueler winner, Dueler loser) {
        Objects.requireNonNull(duel);
        duel.getPlayer1().resetPlayer();
        duel.getPlayer2().resetPlayer();
        activeDuels.remove(duel);

        Bukkit.broadcastMessage(
            playerRepo.getIdentity(winner.getPlayer()) + ChatColor.GOLD
                + " has defeated " + playerRepo.getIdentity(loser.getPlayer()) + ChatColor.GOLD
                + " in a duel"
        );
    }

    @Override
    public Optional<Duel> getActiveDuel(UUID playerId) {
        Objects.requireNonNull(playerId);

        for (Duel duel : activeDuels) {
            if (duel.getPlayer1().getPlayer().getUUID().equals(playerId)
                    || duel.getPlayer2().getPlayer().getUUID().equals(playerId)) {
                return Optional.of(duel);
            }
        }

        return Optional.empty();
    }

    @Override
    public void forfeitDuel(RpPlayer player) {
        Objects.requireNonNull(player);
        Duel duel = getActiveDuel(player.getUUID()).orElse(null);
        Player mcPlayer = player.getPlayer().orElse(null);

        if (mcPlayer == null) {
            return;
        }

        if (duel == null) {
            mcPlayer.sendMessage(ChatColor.RED + "No active duel");
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

        Bukkit.broadcastMessage(
            playerRepo.getIdentity(player) + ChatColor.GOLD + " has fled from "
                + playerRepo.getIdentity(winner) + ChatColor.GOLD + " in a duel"
        );
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

        return configRepo.load().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> Result.ok(null));
    }
}
