package tv.twitch.moonmoon.rpengine2.duel;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import java.util.Objects;
import java.util.Optional;

/**
 * Duel tracking player
 */
public class Dueler {

    private final RpPlayer player;
    private final Location startLocation;
    private final double originalHealth;

    public Dueler(RpPlayer player) {
        this.player = Objects.requireNonNull(player);
        // technically players can log out before we get here due to the countdown, but that's okay
        // because they'll just be removed from the active duels in the event listener
        // so just to be safe we will treat them as nullable here
        startLocation = player.getPlayer()
            .map(Player::getLocation)
            .orElse(null);
        originalHealth = player.getPlayer()
            .map(Player::getHealth)
            .orElse(1.0);
    }

    /**
     * Returns the underlying {@link RpPlayer}
     *
     * @return Player
     */
    public RpPlayer getPlayer() {
        return player;
    }

    /**
     * Returns the {@link Location} the duel started
     *
     * @return Location
     */
    public Optional<Location> getStartLocation() {
        return Optional.of(startLocation);
    }

    /**
     * Returns the original amount of health the player had before starting a duel
     *
     * @return Original health
     */
    public double getOriginalHealth() {
        return originalHealth;
    }

    /**
     * Resets this player to their original state before the duel
     */
    public void resetPlayer() {
        Player p = player.getPlayer().orElse(null);
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
        Dueler dueler = (Dueler) o;
        return Objects.equals(player, dueler.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
