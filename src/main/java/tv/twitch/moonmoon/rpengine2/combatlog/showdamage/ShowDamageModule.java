package tv.twitch.moonmoon.rpengine2.combatlog.showdamage;

import com.google.inject.AbstractModule;

public class ShowDamageModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ShowDamage.class).to(ShowDamageImpl.class);
    }
}
