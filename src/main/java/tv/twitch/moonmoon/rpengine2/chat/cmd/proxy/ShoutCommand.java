package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import javax.inject.Inject;

public class ShoutCommand extends AbstractChannelProxyCommand implements ChannelSayCommand {

    private static final String NOT_CONFIGURED =
        ChatColor.RED + "Shout channel not configured. To use /shout, " +
            "you must have a channel configured named `shout` with the desired range";

    @Inject
    public ShoutCommand(Chat chat, RpPlayerRepo playerRepo) {
        super(chat, playerRepo);
    }

    @Override
    public String getNotConfiguredMessage() {
        return NOT_CONFIGURED;
    }

    @Override
    public String getChannelName() {
        return "shout";
    }
}
