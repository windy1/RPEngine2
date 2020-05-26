package tv.twitch.moonmoon.rpengine2.chat.data.channel;

import tv.twitch.moonmoon.rpengine2.chat.model.ChatChannelConfig;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Set;

public interface ChatChannelConfigDbo {
    Result<Set<ChatChannelConfig>> selectConfigs();

    Result<ChatChannelConfig> selectConfig(int playerId, String channelName);

    Result<Long> createConfig(int playerId, String channelName);

    void setMutedAsync(
        int playerId,
        String channelName,
        boolean muted,
        Callback<Void> callback
    );
}
