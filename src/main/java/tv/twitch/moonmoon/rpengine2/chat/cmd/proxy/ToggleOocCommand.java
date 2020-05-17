package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import javax.inject.Inject;

public class ToggleOocCommand extends AbstractChannelProxyCommand implements ChannelMuteCommand {

    private static final String NOT_CONFIGURED = "OOC channel not configured. " +
        "To use /toggleooc, you must have a channel configured named `ooc`";

    @Inject
    public ToggleOocCommand(Chat chat, RpPlayerRepo playerRepo) {
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
