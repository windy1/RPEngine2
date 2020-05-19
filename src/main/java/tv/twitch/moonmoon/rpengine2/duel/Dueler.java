package tv.twitch.moonmoon.rpengine2.duel;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import java.util.Objects;
import java.util.Optional;

public class Dueler {

    private final RpPlayer player;
    private final Location startLocation;
    private final double originalHealth;

    public Dueler(RpPlayer player) {
        this.player = Objects.requireNonNull(player);
        startLocation = player.getPlayer()
            .map(Player::getLocation)
            .orElse(null);
        originalHealth = player.getPlayer()
            .map(Player::getHealth)
            .orElse(1.0);
    }

    public RpPlayer getPlayer() {
        return player;
    }

    public Optional<Location> getStartLocation() {
        return Optional.of(startLocation);
    }

    public double getOriginalHealth() {
        return originalHealth;
    }

    public void resetPlayer() {
        Player p = player.getPlayer().orElse(null);
        if (p == null) {
            return;
        }

        p.setHealth(originalHealth);
        p.teleport(startLocation);
    }
}
