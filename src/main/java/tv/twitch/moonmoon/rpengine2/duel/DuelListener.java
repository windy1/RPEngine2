package tv.twitch.moonmoon.rpengine2.duel;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
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
    private final Logger log;

    @Inject
    public DuelListener(Duels duels, RpPlayerRepo playerRepo, @PluginLogger Logger log) {
        this.duels = Objects.requireNonNull(duels);
        this.playerRepo = Objects.requireNonNull(playerRepo);
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
        // If a dueler quits mid-duel, they forfeit
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Result<RpPlayer> p = playerRepo.getPlayer(e.getPlayer());

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            log.warning(err.get());
        } else {
            duels.handlePlayerJoined(p.get());
        }
    }
}
