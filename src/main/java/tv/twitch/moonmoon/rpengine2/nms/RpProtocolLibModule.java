package tv.twitch.moonmoon.rpengine2.nms;

import com.google.inject.AbstractModule;

public class RpProtocolLibModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RpProtocolLib.class).to(RpProtocolLibImpl.class);
    }
}
