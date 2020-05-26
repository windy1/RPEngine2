package tv.twitch.moonmoon.rpengine2.spigot.chat.cmd.proxy;

import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.parser.CommandPlayerParser;

import javax.inject.Inject;

public class RpCommand extends AbstractChannelProxyCommand implements ChannelJoinCommand {

    private static final String NOT_CONFIGURED = "RP channel not configured. To use /rp, " +
        "you must have a channel configured named `rp`";

    @Inject
    public RpCommand(Plugin plugin, Chat chat, CommandPlayerParser playerParser) {
        super(plugin, playerParser, chat);
    }

    @Override
    public String getNotConfiguredMessage() {
        return NOT_CONFIGURED;
    }

    @Override
    public String getChannelName() {
        return "rp";
    }

    @Override
    public String getConfigPath() {
        return "chat.commands.rp";
    }
}
