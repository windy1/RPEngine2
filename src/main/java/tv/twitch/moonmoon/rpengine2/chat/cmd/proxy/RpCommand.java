package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import javax.inject.Inject;

public class RpCommand extends AbstractChannelProxyCommand implements ChannelJoinCommand {

    private static final String NOT_CONFIGURED = "RP channel not configured. To use /rp, " +
        "you must have a channel configured named `rp`";

    @Inject
    public RpCommand(Chat chat, RpPlayerRepo playerRepo) {
        super(chat, playerRepo);
    }

    @Override
    public String getNotConfiguredMessage() {
        return NOT_CONFIGURED;
    }

    @Override
    public String getChannelName() {
        return "rp";
    }
}
