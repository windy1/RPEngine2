package tv.twitch.moonmoon.rpengine2.sponge.chat;

import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatModule;

public class SpongeChatModule extends ChatModule {

    @Override
    protected void bindChat(AnnotatedBindingBuilder<Chat> b) {
        b.to(SpongeChat.class);
    }
}
