package tv.twitch.moonmoon.rpengine2.di;

import com.google.inject.AbstractModule;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatImpl;

public class ChatModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Chat.class).to(ChatImpl.class);
    }
}
