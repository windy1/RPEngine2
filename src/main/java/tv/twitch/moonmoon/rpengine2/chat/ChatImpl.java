package tv.twitch.moonmoon.rpengine2.chat;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.chat.cmd.ChatCommands;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatChannelConfig;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.logging.Logger;

@Singleton
public class ChatImpl implements Chat {

    private final JavaPlugin plugin;
    private final RpPlayerRepo playerRepo;
    private final ChatListener listener;
    private final ChatCommands commands;
    private final ChatChannelConfigRepo channelConfigRepo;
    private final Logger log;

    private final Map<String, ChatChannel> channels = new HashMap<>();
    private ChatChannel defaultChannel;
    private int birdSpeed;
    private boolean actionMenu;

    @Inject
    public ChatImpl(
        JavaPlugin plugin,
        RpPlayerRepo playerRepo,
        ChatListener listener,
        ChatCommands commands,
        ChatChannelConfigRepo channelConfigRepo
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.listener = Objects.requireNonNull(listener);
        this.commands = Objects.requireNonNull(commands);
        this.channelConfigRepo = Objects.requireNonNull(channelConfigRepo);
        log = plugin.getLogger();
    }

    @Override
    public Optional<ChatChannel> getChannel(String name) {
        return Optional.ofNullable(channels.get(name));
    }

    @Override
    public Set<ChatChannel> getChannels() {
        return Collections.unmodifiableSet(new HashSet<>(channels.values()));
    }

    @Override
    public Optional<ChatChannel> getDefaultChannel() {
        return Optional.ofNullable(defaultChannel);
    }

    @Override
    public boolean sendMessage(RpPlayer player, String message) {
        ChatChannel channel = player.getChatChannel()
            .orElseGet(() -> getDefaultChannel().orElse(null));

        if (channel == null) {
            return false;
        }

        return sendMessage(player, channel, message);
    }

    @Override
    public boolean sendMessage(RpPlayer player, ChatChannel channel, String message) {
        Player mcSender = player.getPlayer().orElse(null);

        String displayName = playerRepo.getIdentity(player);
        String permission;
        String prefix;
        int range;

        if (channel == null || mcSender == null) {
            return false;
        }

        permission = channel.getPermission().orElse(null);
        prefix = channel.getPrefix();
        range = channel.getRange();

        TextComponent msg = new ChatMessage(
            prefix, displayName, message, channel.getMessageColor(), actionMenu, mcSender.getName()
        ).toTextComponent();

        Objects.requireNonNull(channel);
        Objects.requireNonNull(message);

        Bukkit.getConsoleSender().spigot().sendMessage(msg);

        for (RpPlayer p : playerRepo.getPlayers()) {
            Result<ChatChannelConfig> c = channelConfigRepo.getConfig(p, channel);

            Optional<String> err = c.getError();
            if (err.isPresent()) {
                log.warning(err.get());
                continue;
            }

            Player mcPlayer = p.getPlayer().orElse(null);

            if (mcPlayer == null) {
                continue;
            }

            boolean inRange = range == 0
                || mcPlayer.getLocation().distance(mcSender.getLocation()) <= range;

            boolean canReceive = !c.get().isMuted()
                && (permission == null || mcPlayer.hasPermission(permission))
                && inRange;

            if (canReceive) {
                mcPlayer.spigot().sendMessage(msg);
            }
        }

        return true;
    }

    @Override
    public int getBirdSpeed() {
        return birdSpeed;
    }

    @Override
    public Result<Void> init() {
        commands.register();
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        ConfigurationSection c = plugin.getConfig().getConfigurationSection("chat");
        if (c == null) {
            return Result.error("Invalid configuration file (missing chat section)");
        }

        birdSpeed = c.getInt("birdSpeed", 20);
        actionMenu = c.getBoolean("actionMenu");

        if (birdSpeed <= 0) {
            return Result.error(
                "Invalid configuration file " +
                    "(birdSpeed must be an integer greater than or equal to 1)"
            );
        }

        ConfigurationSection ch = c.getConfigurationSection("channels");

        if (ch != null) {
            loadChannels(ch);
        }

        defaultChannel = channels.get(c.getString("defaultChannel"));
        if (defaultChannel == null) {
            return Result.error(
                "Configured default chat channel was not found in loaded channels"
            );
        }

        return channelConfigRepo.load().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> Result.ok(null));
    }

    @Override
    public void handlePlayerJoined(RpPlayer player) {
        if (!player.getChatChannel().isPresent()) {
            setChatChannelAsync(player, defaultChannel);
        }

        for (ChatChannel channel : getChannels()) {
            channelConfigRepo.getConfig(player, channel).getError()
                .ifPresent(log::warning);
        }
    }

    @Override
    public void setChatChannelAsync(RpPlayer player, ChatChannel channel) {
        playerRepo.setChatChannelAsync(player, channel, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
            } else {
                player.getPlayer().ifPresent(p -> {
                    String permission = channel.getPermission().orElse(null);
                    if (permission != null && !p.hasPermission(permission)) {
                        return;
                    }

                    String message = "%s%sYou are now chatting in [%s]";
                    p.sendMessage(String.format(
                        message, ChatColor.GRAY, ChatColor.ITALIC, channel.getName()
                    ));
                });
            }
        });
    }

    @Override
    public void toggleMutedAsync(RpPlayer player, ChatChannel channel, CommandSender sender) {
        channelConfigRepo.toggleMutedAsync(player, channel, r -> {
            Optional<String> toggleErr = r.getError();
            if (toggleErr.isPresent()) {
                sender.sendMessage(ChatColor.RED + toggleErr.get());
            } else {
                String message = ChatColor.GREEN + (r.get() ? "Muted " : "Unmuted ") +
                    channel.getName();
                sender.sendMessage(message);
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
                c.getString("prefix", ""),
                c.getString("permission", null),
                StringUtils.getSpigotChatColor(c.getString("messageColor"))
                    .orElse(net.md_5.bungee.api.ChatColor.WHITE))
            );
        }
    }
}
