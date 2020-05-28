package tv.twitch.moonmoon.rpengine2.util;

import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

public interface Messenger {

    void warn(String message);

    void info(String message);

    void sendError(RpPlayer player, String message);
}
