package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;

import javax.inject.Inject;

public class ShoutCommand extends AbstractChannelProxyCommand implements ChannelSayCommand {

    private static final String NOT_CONFIGURED =
        ChatColor.RED + "Shout channel not configured. To use /shout, " +
            "you must have a channel configured named `shout` with the desired range";

    @Inject
    public ShoutCommand(Plugin plugin, Chat chat, CommandPlayerParser playerParser) {
        super(plugin, playerParser, chat);
    }

    @Override
    public String getNotConfiguredMessage() {
        return NOT_CONFIGURED;
    }

    @Override
    public String getChannelName() {
        return "shout";
    }

    @Override
    public String getConfigPath() {
        return "chat.commands.shout";
    }
}
