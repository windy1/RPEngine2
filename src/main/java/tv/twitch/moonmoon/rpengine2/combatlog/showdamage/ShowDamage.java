package tv.twitch.moonmoon.rpengine2.combatlog.showdamage;

import org.bukkit.entity.Entity;
import tv.twitch.moonmoon.rpengine2.util.Result;

public interface ShowDamage {

    DamageHologram getHologram(Entity entity);

    void pushDamage(Entity entity, double value);

    void pushRegen(Entity entity, double value);

    Result<Void> init();
}
