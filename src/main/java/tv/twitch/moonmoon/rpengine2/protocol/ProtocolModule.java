package tv.twitch.moonmoon.rpengine2.protocol;

import com.google.inject.AbstractModule;

public class ProtocolModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Protocol.class).to(ProtocolImpl.class);
    }
}
