package tv.twitch.moonmoon.rpengine2.duel;

import com.google.inject.AbstractModule;

public class DuelModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Duels.class).to(DuelsImpl.class);
    }
}
