package tv.twitch.moonmoon.rpengine2.chat.cmd.proxy;

import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;

import java.util.Objects;

public abstract class AbstractChannelProxyCommand extends AbstractCoreCommandExecutor
        implements ChannelProxyCommand {

    private final Chat chat;
    private final RpPlayerRepo playerRepo;

    public AbstractChannelProxyCommand(Plugin plugin, RpPlayerRepo playerRepo, Chat chat) {
        super(plugin);
        this.chat = Objects.requireNonNull(chat);
        this.playerRepo = Objects.requireNonNull(playerRepo);
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
    public RpPlayerRepo getPlayerRepo() {
        return playerRepo;
    }
}
