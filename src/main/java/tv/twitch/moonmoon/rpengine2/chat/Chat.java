package tv.twitch.moonmoon.rpengine2.chat;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import java.util.Optional;

public interface Chat {

    Optional<ChatChannel> getChannel(String name);

    Optional<ChatChannel> getDefaultChannel();

    void sendMessage(ChatChannel channel, String message);

    void load();

    void handlePlayerJoined(RpPlayer player);
}
