package tv.twitch.moonmoon.rpengine2.sponge.chat;

import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class SpongeChat implements Chat {

    @Override
    public Optional<ChatChannel> getChannel(String name) {
        // TODO
        return Optional.empty();
    }

    @Override
    public Set<ChatChannel> getChannels() {
        // TODO
        return Collections.emptySet();
    }

    @Override
    public Optional<ChatChannel> getDefaultChannel() {
        // TODO
        return Optional.empty();
    }

    @Override
    public boolean sendMessage(RpPlayer sender, String message) {
        // TODO
        return false;
    }

    @Override
    public boolean sendMessage(RpPlayer sender, ChatChannel channel, String message) {
        // TODO
        return false;
    }

    @Override
    public int getBirdSpeed() {
        // TODO
        return 0;
    }

    @Override
    public void setChatChannelAsync(RpPlayer player, ChatChannel channel) {
        // TODO
    }

    @Override
    public Result<Void> init() {
        return Result.ok(null);
    }

    @Override
    public void handlePlayerJoined(RpPlayer player) {
        // TODO
    }
}
