package tv.twitch.moonmoon.rpengine2.combatlog.showdamage;

import org.bukkit.entity.Entity;
import tv.twitch.moonmoon.rpengine2.util.Result;

/**
 * Module that manages damage-display related functionality
 */
public interface ShowDamage {

    /**
     * Returns a tracked {@link DamageHologram} for the specified {@link Entity}
     *
     * @param entity Entity
     * @return Hologram if found, empty otherwise
     */
    DamageHologram getHologram(Entity entity);

    /**
     * Pushes a damage update for the specified {@link Entity}
     *
     * @param entity Entity
     * @param value Damage amount
     */
    void pushDamage(Entity entity, double value);

    /**
     * Pushes a regen update for the specified {@link Entity}
     *
     * @param entity Entity
     * @param value Regen amount
     */
    void pushRegen(Entity entity, double value);

    Result<Void> init();
}
