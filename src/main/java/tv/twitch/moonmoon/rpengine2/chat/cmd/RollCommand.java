package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RollCommand extends AbstractCoreCommandExecutor {

    private final RpPlayerRepo playerRepo;

    @Inject
    public RollCommand(Plugin plugin, RpPlayerRepo playerRepo) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        ConfigurationSection c = plugin.getConfig().getConfigurationSection("chat.roll");
        if (c == null) {
            sender.sendMessage(ChatColor.RED + "Invalid configuration (missing roll section)");
            return true;
        }

        String prefix = c.getString("prefix", "");
        int range = c.getInt("range", 0);
        String minStr = "1";
        String maxStr = "100";
        int min;
        int max;
        Player mcPlayer = (Player) sender;
        Result<RpPlayer> p = playerRepo.getPlayer(mcPlayer);
        RpPlayer player;
        Location playerLocation = mcPlayer.getLocation();

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }
        player = p.get();

        if (args.length > 1) {
            minStr = args[0];
            maxStr = args[1];
        } else if (args.length == 1) {
            maxStr = args[0];
        }

        try {
            min = Integer.parseInt(minStr);
            max = Integer.parseInt(maxStr);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number argument");
            return false;
        }

        int roll = ThreadLocalRandom.current().nextInt(min, max + 1);
        String message = String.format(
            "%s%s %srolls a %s%d",
            prefix, playerRepo.getIdentity(player), ChatColor.LIGHT_PURPLE, ChatColor.GREEN, roll
        );

        for (Player q : Bukkit.getOnlinePlayers()) {
            if (range == 0 || q.getLocation().distance(playerLocation) <= range) {
                q.sendMessage(message);
            }
        }

        return true;
    }

    @Override
    public String getConfigPath() {
        return "chat.commands.roll";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
