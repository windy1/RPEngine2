package tv.twitch.moonmoon.rpengine2.spigot.cmd;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public interface CoreCommandExecutor extends CommandExecutor {

    Plugin getPlugin();

    String getConfigPath();

    Optional<Instant> getLastExecution(OfflinePlayer sender);

    void setLastExecution(OfflinePlayer sender, Instant instant);

    boolean isPlayerOnly();

    @Override
    default boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (isPlayerOnly()) {
                sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
                return true;
            }
            return handle(sender, args);
        }

        Player player = (Player) sender;
        int cooldown = getPlugin().getConfig().getInt(getConfigPath() + ".cooldown", 0);
        Optional<Instant> i = getLastExecution(player);
        Instant lastExecution;

        if (cooldown == 0) {
            return handle(sender, args);
        }

        if (!i.isPresent()) {
            setLastExecution(player, Instant.now());
            return handle(sender, args);
        }

        lastExecution = i.get();
        float secondsElapsed = Duration.between(lastExecution, Instant.now()).toMillis() / 1000f;

        if (secondsElapsed < cooldown) {
            sender.sendMessage(ChatColor.RED + String.format(
                "You must wait another %d seconds before executing this command",
                (int) Math.floor(cooldown - secondsElapsed)
            ));
            return true;
        }

        setLastExecution(player, Instant.now());

        return handle(sender, args);
    }

    boolean handle(CommandSender sender, String[] args);
}
