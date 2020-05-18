package tv.twitch.moonmoon.rpengine2.nte;

import com.google.inject.AbstractModule;

public class NametagEditModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(NametagEditPlugin.class).to(NametagEditPluginImpl.class);
    }
}
