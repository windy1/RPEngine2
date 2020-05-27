package tv.twitch.moonmoon.rpengine2.spigot.chat;

import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatModule;

public class SpigotChatModule extends ChatModule {

    @Override
    protected void bindChat(AnnotatedBindingBuilder<Chat> b) {
        b.to(SpigotChatImpl.class);
    }
}
