package tv.twitch.moonmoon.rpengine2.spigot.chat;

import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatModule;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatConfigDbo;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.ChatChannelConfigDbo;
import tv.twitch.moonmoon.rpengine2.spigot.chat.data.SpigotChatConfigDbo;
import tv.twitch.moonmoon.rpengine2.spigot.chat.data.channel.SpigotChatChannelConfigDbo;

public class SpigotChatModule extends ChatModule {

    @Override
    protected void bindChat(AnnotatedBindingBuilder<Chat> b) {
        b.to(SpigotChatImpl.class);
    }

    @Override
    protected void bindConfigDbo(AnnotatedBindingBuilder<ChatConfigDbo> b) {
        b.to(SpigotChatConfigDbo.class);
    }

    @Override
    protected void bindChannelConfigDbo(AnnotatedBindingBuilder<ChatChannelConfigDbo> b) {
        b.to(SpigotChatChannelConfigDbo.class);
    }
}
