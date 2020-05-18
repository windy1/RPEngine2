package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import javax.inject.Inject;
import java.util.Objects;

public class WhisperCommand extends AbstractChannelProxyCommand implements ChannelSayCommand {

    private static final String NOT_CONFIGURED =
        ChatColor.RED + "Whisper channel not configured. To use /whisper, " +
            "you must have a channel configured named `whisper` with the desired range";

    @Inject
    public WhisperCommand(Plugin plugin, Chat chat, RpPlayerRepo playerRepo) {
        super(plugin, playerRepo, chat);
    }

    @Override
    public String getNotConfiguredMessage() {
        return NOT_CONFIGURED;
    }

    @Override
    public String getChannelName() {
        return "whisper";
    }

    @Override
    public String getConfigPath() {
        return "chat.commands.whisper";
    }
}
