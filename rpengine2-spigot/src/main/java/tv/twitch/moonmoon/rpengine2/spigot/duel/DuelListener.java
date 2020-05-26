package tv.twitch.moonmoon.rpengine2.spigot.duel;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duel;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.Dueler;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class DuelListener implements Listener {

    private final Duels duels;
    private final RpPlayerRepo playerRepo;
    private final DuelInvites invites;
    private final Logger log;

    @Inject
    public DuelListener(
        Duels duels,
        RpPlayerRepo playerRepo,
        DuelInvites invites,
        @PluginLogger Logger log
    ) {
        this.duels = Objects.requireNonNull(duels);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.invites = Objects.requireNonNull(invites);
        this.log = Objects.requireNonNull(log);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        Player player;
        Player damagerPlayer;
        UUID damagerId;
        Duel activeDuel;
        Dueler p1;
        Dueler p2;

        if (!(entity instanceof Player)) {
            // non-player was damaged
            return;
        }

        player = (Player) entity;
        activeDuel = duels.getActiveDuel(player.getUniqueId()).orElse(null);

        if (activeDuel == null) {
            // player is not dueling
            return;
        }

        if (!(damager instanceof Player)) {
            // damaged by non-player
            e.setCancelled(false);
            return;
        }

        if (!activeDuel.hasStarted()) {
            // countdown in progress
            e.setCancelled(true);
            return;
        }

        damagerPlayer = (Player) damager;
        damagerId = damagerPlayer.getUniqueId();
        p1 = activeDuel.getPlayer1();
        p2 = activeDuel.getPlayer2();

        // If in a duel, make sure the damage is not from a player not in the duel
        if (!p1.getPlayer().getUUID().equals(damagerId)
            && !p2.getPlayer().getUUID().equals(damagerId)) {
            e.setCancelled(true);
            return;
        }

        // Check if a dueler will die
        if (player.getHealth() - e.getFinalDamage() <= 0) {
            Dueler winner;
            Dueler loser;
            if (p1.getPlayer().getUUID().equals(damagerId)) {
                winner = p1;
                loser = p2;
            } else {
                winner = p2;
                loser = p1;
            }

            duels.endDuel(activeDuel, winner, loser);
            e.setCancelled(true);

            return;
        }

        e.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player mcPlayer = e.getPlayer();
        UUID playerId = mcPlayer.getUniqueId();
        invites.clear(playerId);

        // If a dueler quits mid-duel, they forfeit
        Duel duel = duels.getActiveDuel(playerId).orElse(null);
        Result<RpPlayer> player = playerRepo.getPlayer(mcPlayer.getUniqueId());

        if (duel == null) {
            return;
        }

        Optional<String> err = player.getError();
        if (err.isPresent()) {
            log.warning(err.get());
        } else {
            duels.forfeitDuel(player.get());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Result<RpPlayer> p = playerRepo.getPlayer(e.getPlayer().getUniqueId());

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            log.warning(err.get());
        } else {
            duels.handlePlayerJoined(p.get());
        }
    }
}
