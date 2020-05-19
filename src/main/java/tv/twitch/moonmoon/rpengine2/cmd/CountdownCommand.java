package tv.twitch.moonmoon.rpengine2.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CountdownCommand extends AbstractCoreCommandExecutor {

    @Inject
    public CountdownCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        FileConfiguration config = plugin.getConfig();
        int timeSecs = 3;
        int range = config.getInt("countdown.range", 0);
        Location playerLocation = ((Player) sender).getLocation();

        if (args.length > 0) {
            try {
                timeSecs = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number argument");
                return false;
            }
        }

        if (timeSecs <= 0) {
            sender.sendMessage("Time must be greater than or equal to 1");
            return true;
        }

        Set<UUID> playerIds = Bukkit.getOnlinePlayers().stream()
            .filter(p -> range == 0 || p.getLocation().distance(playerLocation) <= range)
            .map(Player::getUniqueId)
            .collect(Collectors.toSet());

        Countdown.from(config, playerIds, timeSecs, null).start();

        return true;
    }

    @Override
    public String getConfigPath() {
        return "commands.countdown";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
