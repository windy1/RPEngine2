package tv.twitch.moonmoon.rpengine2.spigot.cmd.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;
import java.util.Objects;

public class PlayedCommand extends AbstractCoreCommandExecutor {

    private final CommandPlayerParser playerParser;

    @Inject
    public PlayedCommand(Plugin plugin, CommandPlayerParser playerParser) {
        super(plugin);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        RpPlayer player = playerParser.parse(sender).orElse(null);

        if (player == null) {
            return true;
        }

        long seconds = player.getPlayedLive().toMillis() / 1000;

        long weeks = seconds / 604800;
        long days = (seconds % 604800) / 86400;
        long hours = ((seconds % 604800) % 86400) / 3600;
        long minutes = (((seconds % 604800) % 86400) % 3600) / 60;
        seconds = (((seconds % 604800) % 86400) % 3600) % 60;

        StringBuilder out = new StringBuilder();

        if (weeks > 0) {
            out.append(weeks).append(" weeks");
        }

        if (days > 0) {
            if (out.length() > 0) {
                out.append(", ");
            }
            out.append(days).append(" days");
        }

        if (hours > 0) {
            if (out.length() > 0) {
                out.append(", ");
            }
            out.append(hours).append(" hours");
        }

        if (minutes > 0) {
            if (out.length() > 0) {
                out.append(", ");
            }
            out.append(minutes).append(" minutes");
        }

        if (seconds > 0) {
            if (out.length() > 0) {
                out.append(", ");
            }
            out.append(seconds).append(" seconds");
        }

        sender.sendMessage(ChatColor.AQUA + out.toString());

        return true;
    }

    @Override
    public String getConfigPath() {
        return "commands.played";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
