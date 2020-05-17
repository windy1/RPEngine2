package tv.twitch.moonmoon.rpengine2.chat;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.Set;

public interface Chat {

    Optional<ChatChannel> getChannel(String name);

    Set<ChatChannel> getChannels();

    Optional<ChatChannel> getDefaultChannel();

    boolean sendMessage(RpPlayer player, String message);

    boolean sendMessage(RpPlayer player, ChatChannel channel, String message);

    int getBirdSpeed();

    Result<Void> load();

    void handlePlayerJoined(RpPlayer player);

    void setChatChannelAsync(RpPlayer player, ChatChannel channel);
}
