package tv.twitch.moonmoon.rpengine2.chat.data;

import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatConfig;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.task.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Set;

public interface ChatConfigRepo extends Repo {

    Result<ChatConfig> getConfig(RpPlayer player);

    Set<ChatConfig> getConfigs();

    void setChannelAsync(
        RpPlayer player,
        ChatChannel channel,
        Callback<Void> callback
    );
}
