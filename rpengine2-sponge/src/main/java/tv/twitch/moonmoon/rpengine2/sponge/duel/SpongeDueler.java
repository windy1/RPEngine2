package tv.twitch.moonmoon.rpengine2.sponge.duel;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import tv.twitch.moonmoon.rpengine2.duel.Dueler;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.sponge.model.player.SpongeRpPlayer;

import java.util.Objects;

public class SpongeDueler implements Dueler {

    private final RpPlayer player;
    private final Location<World> startLocation;
    private final double originalHealth;

    public SpongeDueler(RpPlayer player) {
        this.player = Objects.requireNonNull(player);
        SpongeRpPlayer sPlayer = (SpongeRpPlayer) player;
        startLocation = sPlayer.getPlayer()
            .map(Player::getLocation)
            .orElse(null);
        originalHealth = sPlayer.getPlayer()
            .map(Player::getHealthData)
            .map(h -> h.health().get())
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
        Player p = ((SpongeRpPlayer) player).getPlayer().orElse(null);
        if (p == null) {
            return;
        }

        p.getHealthData().health().set(originalHealth);
        p.setLocationSafely(startLocation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpongeDueler that = (SpongeDueler) o;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
