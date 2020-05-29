package tv.twitch.moonmoon.rpengine2.chat.data;

import tv.twitch.moonmoon.rpengine2.chat.model.ChatConfig;
import tv.twitch.moonmoon.rpengine2.task.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Set;

public interface ChatConfigDbo {
    Result<Set<ChatConfig>> selectConfigs();

    Result<ChatConfig> selectConfig(int playerId);

    Result<Long> createConfig(int playerId);

    void updateChannelAsync(int playerId, String channelName, Callback<Void> callback);
}
