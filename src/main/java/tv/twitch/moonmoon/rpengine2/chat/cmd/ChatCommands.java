package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.chat.cmd.proxy.ShoutCommand;
import tv.twitch.moonmoon.rpengine2.chat.cmd.proxy.WhisperCommand;
import tv.twitch.moonmoon.rpengine2.cmd.Commands;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatCommands implements Commands {

    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> executors = new HashMap<>();

    @Inject
    public ChatCommands(
        JavaPlugin plugin,
        ChannelCommand channelCommand,
        BirdCommand birdCommand,
        ShoutCommand shoutCommand,
        WhisperCommand whisperCommand
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        executors.put("channel", channelCommand);
        executors.put("bird", birdCommand);
        executors.put("shout", shoutCommand);
        executors.put("whisper", whisperCommand);
    }

    @Override
    public Map<String, CommandExecutor> getExecutors() {
        return executors;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getConfigPath() {
        return "chat.commands";
    }
}
