package tv.twitch.moonmoon.rpengine2.duel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.Countdown;
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
    private final Set<Duel> activeDuels = Collections.synchronizedSet(new HashSet<>());
    private final Logger log;

    @Inject
    public DuelsImpl(
        Plugin plugin,
        DuelCommands commands,
        DuelListener listener,
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.commands = Objects.requireNonNull(commands);
        this.listener = Objects.requireNonNull(listener);
        this.configRepo = Objects.requireNonNull(configRepo);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        log = plugin.getLogger();
    }

    @Override
    public void startDuel(RpPlayer p1, RpPlayer p2) {
        Set<UUID> playerIds = new HashSet<>(Arrays.asList(p1.getUUID(), p2.getUUID()));
        Countdown.from(plugin.getConfig(), playerIds, 3, () ->
            activeDuels.add(new Duel(new Dueler(p1), new Dueler(p2)))
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
    public void handlePlayerJoined(RpPlayer player) {
        configRepo.getConfig(player).getError().ifPresent(log::warning);
    }

    @Override
    public Result<Void> init() {
        commands.register();
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        return configRepo.load().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> Result.ok(null));
    }
}
