package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;

import java.util.Objects;

public abstract class AbstractChannelProxyCommand extends AbstractCoreCommandExecutor
        implements ChannelProxyCommand {

    private final Chat chat;
    private final CommandPlayerParser playerParser;

    public AbstractChannelProxyCommand(
        Plugin plugin,
        CommandPlayerParser playerParser,
        Chat chat
    ) {
        super(plugin);
        this.chat = Objects.requireNonNull(chat);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public Chat getChat() {
        return chat;
    }

    @Override
    public CommandPlayerParser getPlayerParser() {
        return playerParser;
    }
}
