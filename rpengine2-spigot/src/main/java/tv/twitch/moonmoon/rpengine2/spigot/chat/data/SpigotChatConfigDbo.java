package tv.twitch.moonmoon.rpengine2.spigot.chat.data;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.data.AbstractChatConfigDbo;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class SpigotChatConfigDbo extends AbstractChatConfigDbo {

    private final Plugin plugin;

    @Inject
    public SpigotChatConfigDbo(Plugin plugin, RpDb db, Chat chat) {
        super(db, chat);
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void updateChannelAsync(int playerId, String channelName, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(updateChannel(playerId, channelName))
        );
    }
}
