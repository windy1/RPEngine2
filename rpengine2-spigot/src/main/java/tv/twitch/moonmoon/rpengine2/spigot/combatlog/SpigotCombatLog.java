package tv.twitch.moonmoon.rpengine2.spigot.combatlog;

import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.spigot.combatlog.showdamage.ShowDamage;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Optional;

public class SpigotCombatLog implements CombatLog {

    private final ShowDamage showDamage;

    @Inject
    public SpigotCombatLog(Optional<ShowDamage> showDamage) {
        this.showDamage = showDamage.orElse(null);
    }

    public Optional<ShowDamage> getShowDamageModule() {
        return Optional.ofNullable(showDamage);
    }

    @Override
    public Result<Void> init() {
        if (showDamage != null) {
            Optional<String> err = showDamage.init().getError();
            if (err.isPresent()) {
                return Result.error(err.get());
            }
        }

        return Result.ok(null);
    }
}
