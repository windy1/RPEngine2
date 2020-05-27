package tv.twitch.moonmoon.rpengine2.sponge.combatlog;

import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLogModule;

public class SpongeCombatLogModule extends CombatLogModule {

    @Override
    protected void bindCombatLog(AnnotatedBindingBuilder<CombatLog> b) {
        b.to(SpongeCombatLog.class);
    }

    @Override
    protected void configure() {
        super.configure();

        // TODO
    }
}
