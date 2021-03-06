package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;

import javax.inject.Inject;

public class ToggleOocCommand extends AbstractChannelProxyCommand implements ChannelMuteCommand {

    private static final String NOT_CONFIGURED = "OOC channel not configured. " +
        "To use /toggleooc, you must have a channel configured named `ooc`";

    @Inject
    public ToggleOocCommand(Plugin plugin, Chat chat, CommandPlayerParser playerParser) {
        super(plugin, playerParser, chat);
    }

    @Override
    public String getNotConfiguredMessage() {
        return NOT_CONFIGURED;
    }

    @Override
    public String getChannelName() {
        return "ooc";
    }

    @Override
    public String getConfigPath() {
        return "chat.commands.toggleooc";
    }
}
