package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import javax.inject.Inject;

public class OocCommand extends AbstractChannelProxyCommand implements ChannelJoinCommand {

    private static final String NOT_CONFIGURED = "OOC channel not configured. To use /ooc, " +
        "you must have a channel configured named `ooc`";

    @Inject
    public OocCommand(Chat chat, RpPlayerRepo playerRepo) {
        super(chat, playerRepo);
    }

    @Override
    public String getNotConfiguredMessage() {
        return NOT_CONFIGURED;
    }

    @Override
    public String getChannelName() {
        return "ooc";
    }
}
