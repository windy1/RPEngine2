package tv.twitch.moonmoon.rpengine2.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class ChatImpl implements Chat {

    private final JavaPlugin plugin;
    private final RpPlayerRepo playerRepo;
    private final ChatListener listener;
    private final Logger log;

    private final Map<String, ChatChannel> channels = new HashMap<>();
    private ChatChannel defaultChannel;
    private int whisperRange;
    private int shoutRange;
    private int birdSpeed;

    @Inject
    public ChatImpl(JavaPlugin plugin, RpPlayerRepo playerRepo, ChatListener listener) {
        this.plugin = Objects.requireNonNull(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.listener = Objects.requireNonNull(listener);
        log = plugin.getLogger();
    }

    @Override
    public Optional<ChatChannel> getChannel(String name) {
        return Optional.ofNullable(channels.get(name));
    }

    @Override
    public Optional<ChatChannel> getDefaultChannel() {
        return Optional.ofNullable(defaultChannel);
    }

    @Override
    public void sendMessage(ChatChannel channel, String message) {
        Objects.requireNonNull(channel);
        Objects.requireNonNull(message);

        for (RpPlayer player : playerRepo.getPlayers()) {
            // TODO: muted channels
            player.getPlayer().ifPresent(p -> p.sendMessage(message));
        }
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        ConfigurationSection c = plugin.getConfig().getConfigurationSection("chat");
        if (c == null) {
            log.warning("Invalid configuration file (missing chat section)");
            return;
        }

        whisperRange = c.getInt("whisperRange", 0);
        shoutRange = c.getInt("shoutRange", 0);
        birdSpeed = c.getInt("birdSpeed", 0);

        ConfigurationSection ch = c.getConfigurationSection("channels");

        if (ch != null) {
            loadChannels(ch);
        }

        defaultChannel = channels.get(c.getString("defaultChannel"));
        if (defaultChannel == null) {
            log.warning("Configured default chat channel was not found in loaded channels");
        }
    }

    @Override
    public void handlePlayerJoined(RpPlayer player) {
        if (!player.getChatChannel().isPresent()) {
            setChatChannelAsync(player, defaultChannel);
        }
    }

    private void setChatChannelAsync(RpPlayer player, ChatChannel channel) {
        playerRepo.setChatChannelAsync(player, channel, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
            } else {
                player.getPlayer().ifPresent(p -> {
                    String message = "%s%sYou are now chatting in [%s]";
                    p.sendMessage(String.format(
                        message, ChatColor.GRAY, ChatColor.ITALIC, channel.getName()
                    ));
                });
            }
        });
    }

    private void loadChannels(ConfigurationSection ch) {
        for (String channelName : ch.getKeys(false)) {
            ConfigurationSection c = ch.getConfigurationSection(channelName);
            Objects.requireNonNull(c);

            channels.put(channelName, new ChatChannel(
                channelName,
                c.getInt("range", 0),
                c.getString("prefix")
            ));
        }
    }
}
