package tv.twitch.moonmoon.rpengine2.combatlog.showdamage;

import com.sainttx.holograms.api.Hologram;

import java.time.Instant;
import java.util.Objects;

public class DamageHologram {

    private final Hologram hologram;
    private final Instant spawnTime;

    public DamageHologram(Hologram hologram) {
        this.hologram = Objects.requireNonNull(hologram);
        spawnTime = Instant.now();
    }

    public Hologram getHologram() {
        return hologram;
    }

    public Instant getSpawnTime() {
        return spawnTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DamageHologram that = (DamageHologram) o;
        return Objects.equals(hologram.getId(), that.hologram.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(hologram.getId());
    }

    @Override
    public String toString() {
        return "DamageHologram{" +
            "hologram=" + hologram +
            ", spawnTime=" + spawnTime +
            '}';
    }
}
