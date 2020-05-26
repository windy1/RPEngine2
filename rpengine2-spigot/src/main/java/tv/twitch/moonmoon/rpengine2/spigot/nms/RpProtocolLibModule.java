package tv.twitch.moonmoon.rpengine2.spigot.nms;

import com.google.inject.AbstractModule;

public class RpProtocolLibModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RpProtocolLib.class).to(RpProtocolLibImpl.class);
    }
}
