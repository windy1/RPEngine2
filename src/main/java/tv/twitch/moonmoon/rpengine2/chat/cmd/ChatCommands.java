package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import tv.twitch.moonmoon.rpengine2.chat.cmd.proxy.*;
import tv.twitch.moonmoon.rpengine2.cmd.Commands;
import tv.twitch.moonmoon.rpengine2.cmd.util.RollCommand;

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
        WhisperCommand whisperCommand,
        OocCommand oocCommand,
        RpCommand rpCommand,
        ToggleOocCommand toggleOocCommand,
        EmoteCommand emoteCommand
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        executors.put("channel", channelCommand);
        executors.put("bird", birdCommand);
        executors.put("shout", shoutCommand);
        executors.put("whisper", whisperCommand);
        executors.put("ooc", oocCommand);
        executors.put("rp", rpCommand);
        executors.put("toggleooc", toggleOocCommand);
        executors.put("emote", emoteCommand);
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
