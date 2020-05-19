package tv.twitch.moonmoon.rpengine2.chat;

import com.google.inject.AbstractModule;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatConfigRepoImpl;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.ChatChannelConfigRepoImpl;

public class ChatModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Chat.class).to(ChatImpl.class);
        bind(ChatConfigRepo.class).to(ChatConfigRepoImpl.class);
        bind(ChatChannelConfigRepo.class).to(ChatChannelConfigRepoImpl.class);
    }
}
