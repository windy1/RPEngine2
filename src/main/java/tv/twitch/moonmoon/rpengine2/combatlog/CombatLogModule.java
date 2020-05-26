package tv.twitch.moonmoon.rpengine2.combatlog;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

public abstract class CombatLogModule extends AbstractModule {

    @Override
    protected void configure() {
        bindCombatLog(bind(CombatLog.class));
    }

    protected abstract void bindCombatLog(AnnotatedBindingBuilder<CombatLog> b);
}
