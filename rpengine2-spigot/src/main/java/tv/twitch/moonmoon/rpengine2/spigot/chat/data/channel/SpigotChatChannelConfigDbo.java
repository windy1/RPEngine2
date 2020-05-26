package tv.twitch.moonmoon.rpengine2.spigot.chat.data.channel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.AbstractChatChannelConfigDbo;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.ChatChannelConfigDbo;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatChannelConfig;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Singleton
public class SpigotChatChannelConfigDbo extends AbstractChatChannelConfigDbo {

    private final Plugin plugin;

    @Inject
    public SpigotChatChannelConfigDbo(Plugin plugin, RpDb db) {
        super(db);
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void setMutedAsync(
        int playerId,
        String channelName,
        boolean muted,
        Callback<Void> callback
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(setMuted(playerId, channelName, muted))
        );
    }
}
