package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import javax.inject.Inject;
import java.util.Objects;

public class ShoutCommand implements ChannelProxyCommand {

    private static final String NOT_CONFIGURED =
        ChatColor.RED + "Shout channel not configured. To use /shout, " +
            "you must have a channel configured named `shout` with the desired range";

    private final Chat chat;
    private final RpPlayerRepo playerRepo;

    @Inject
    public ShoutCommand(Chat chat, RpPlayerRepo playerRepo) {
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

    @Override
    public String getNotConfiguredMessage() {
        return NOT_CONFIGURED;
    }

    @Override
    public String getChannelName() {
        return "shout";
    }
}
