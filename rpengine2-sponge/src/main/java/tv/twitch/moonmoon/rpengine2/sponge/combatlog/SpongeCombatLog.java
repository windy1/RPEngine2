package tv.twitch.moonmoon.rpengine2.sponge.combatlog;

import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;

public class SpongeCombatLog implements CombatLog {

    @Inject
    public SpongeCombatLog() {
    }

    @Override
    public Result<Void> init() {
        // TODO
        return Result.ok(null);
    }
}
