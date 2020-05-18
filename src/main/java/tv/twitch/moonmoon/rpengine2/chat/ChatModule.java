package tv.twitch.moonmoon.rpengine2.chat;

import com.google.inject.AbstractModule;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatChannelConfigRepoImpl;

public class ChatModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Chat.class).to(ChatImpl.class);
        bind(ChatChannelConfigRepo.class).to(ChatChannelConfigRepoImpl.class);
    }
}
