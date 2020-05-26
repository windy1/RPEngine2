package tv.twitch.moonmoon.rpengine2.chat;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatConfigDbo;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.data.CoreChatConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.ChatChannelConfigDbo;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.CoreChatChannelConfigRepo;

public abstract class ChatModule extends AbstractModule {

    @Override
    protected void configure() {
        bindChat(bind(Chat.class));
        bind(ChatConfigRepo.class).to(CoreChatConfigRepo.class);
        bind(ChatChannelConfigRepo.class).to(CoreChatChannelConfigRepo.class);
        bindConfigDbo(bind(ChatConfigDbo.class));
        bindChannelConfigDbo(bind(ChatChannelConfigDbo.class));
    }

    protected abstract void bindChat(AnnotatedBindingBuilder<Chat> b);

    protected abstract void bindConfigDbo(AnnotatedBindingBuilder<ChatConfigDbo> b);

    protected abstract void bindChannelConfigDbo(AnnotatedBindingBuilder<ChatChannelConfigDbo> b);
}
