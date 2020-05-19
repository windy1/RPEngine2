package tv.twitch.moonmoon.rpengine2.combatlog;

import tv.twitch.moonmoon.rpengine2.combatlog.showdamage.ShowDamage;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Optional;

public class CombatLogImpl implements CombatLog {

    private final ShowDamage showDamage;

    @Inject
    public CombatLogImpl(Optional<ShowDamage> showDamage) {
        this.showDamage = showDamage.orElse(null);
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
