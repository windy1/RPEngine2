package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import java.util.Objects;

public abstract class AbstractChannelProxyCommand implements ChannelProxyCommand {

    private final Chat chat;
    private final RpPlayerRepo playerRepo;

    public AbstractChannelProxyCommand(Chat chat, RpPlayerRepo playerRepo) {
        this.chat = Objects.requireNonNull(chat);
        this.playerRepo = Objects.requireNonNull(playerRepo);
    }

    @Override
    public Chat getChat() {
        return chat;
    }

    @Override
    public RpPlayerRepo getPlayerRepo() {
        return playerRepo;
    }
}
