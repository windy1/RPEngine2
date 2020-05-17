package tv.twitch.moonmoon.rpengine2.chat;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;

public interface Chat {

    Optional<ChatChannel> getChannel(String name);

    Optional<ChatChannel> getDefaultChannel();

    boolean sendMessage(RpPlayer player, String message);

    Result<Void> load();

    void handlePlayerJoined(RpPlayer player);
}
