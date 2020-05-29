package tv.twitch.moonmoon.rpengine2.chat.data.channel;

import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatChannelConfig;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.task.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Map;
import java.util.Set;

public interface ChatChannelConfigRepo extends Repo {

    Result<ChatChannelConfig> getConfig(RpPlayer player, ChatChannel channel);

    Set<Map<String, ChatChannelConfig>> getConfigs();

    void toggleMutedAsync(RpPlayer player, ChatChannel channel, Callback<Boolean> callback);
}
