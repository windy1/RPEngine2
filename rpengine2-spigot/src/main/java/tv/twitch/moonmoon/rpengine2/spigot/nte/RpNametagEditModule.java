package tv.twitch.moonmoon.rpengine2.spigot.nte;

import com.google.inject.AbstractModule;

public class RpNametagEditModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RpNametagEdit.class).to(RpNametagEditImpl.class);
    }
}
