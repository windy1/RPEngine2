package tv.twitch.moonmoon.rpengine2.spigot.duel.dueler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.model.player.SpigotRpPlayer;

import java.util.Objects;

/**
 * Duel tracking player
 */
public class SpigotDueler implements Dueler {

    private final RpPlayer player;
    private final Location startLocation;
    private final double originalHealth;

    public SpigotDueler(RpPlayer player) {
        this.player = Objects.requireNonNull(player);
        // technically players can log out before we get here due to the countdown, but that's okay
        // because they'll just be removed from the active duels in the event listener
        // so just to be safe we will treat them as nullable here
        SpigotRpPlayer sPlayer = (SpigotRpPlayer) player;
        startLocation = sPlayer.getPlayer()
            .map(Player::getLocation)
            .orElse(null);
        originalHealth = sPlayer.getPlayer()
            .map(Player::getHealth)
            .orElse(1.0);
    }

    @Override
    public RpPlayer getPlayer() {
        return player;
    }

    @Override
    public double getOriginalHealth() {
        return originalHealth;
    }

    @Override
    public void resetPlayer() {
        Player p = ((SpigotRpPlayer) player).getPlayer().orElse(null);
        if (p == null) {
            return;
        }

        p.setHealth(originalHealth);
        p.teleport(startLocation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpigotDueler dueler = (SpigotDueler) o;
        return Objects.equals(player, dueler.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
