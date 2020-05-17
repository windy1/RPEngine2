package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BirdCommand implements CommandExecutor {

    private final Plugin plugin;
    private final Chat chat;
    private final RpPlayerRepo playerRepo;

    @Inject
    public BirdCommand(Plugin plugin, Chat chat, RpPlayerRepo playerRepo) {
        this.plugin = Objects.requireNonNull(plugin);
        this.chat = Objects.requireNonNull(chat);
        this.playerRepo = Objects.requireNonNull(playerRepo);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        String targetName = args[0];
        String message = String.join(" ", StringUtils.splice(args, 1));
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(targetName);
        Result<RpPlayer> p = playerRepo.getPlayer(player);
        PlayerInventory inv = player.getInventory();
        double distance;
        int tripTimeTicks;
        UUID targetId;

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        int paperIndex = inv.first(Material.PAPER);
        if (paperIndex == -1) {
            sender.sendMessage(
                "" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC +
                    "You feel as though you've run out of paper"
            );
            return true;
        } else {
            ItemStack paper = inv.getItem(paperIndex);
            if (paper != null) {
                paper.setAmount(paper.getAmount() - 1);
            }
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot send a bird to yourself");
            return true;
        }

        distance = player.getLocation().distance(target.getLocation());
        tripTimeTicks = (int) Math.round(distance / chat.getBirdSpeed() * 20);
        targetId = target.getUniqueId();

        Bukkit.getScheduler().runTaskLater(plugin, () ->
            receiveBird(playerRepo.getIdentity(p.get()), targetId, message),
            tripTimeTicks
        );

        player.sendMessage(
            "" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "Your bird takes flight"
        );

        return true;
    }

    private void receiveBird(String playerName, UUID targetId, String message) {
        Player t = Bukkit.getPlayer(targetId);
        Player p = Bukkit.getPlayer(playerName);
        if (t == null && p != null) {
            p.sendMessage(
                "" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC +
                    "Your bird arrives only to find it's target has mysteriously disappeared..."
            );
        } else if (t != null) {
            t.sendMessage(
                "" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC +
                    "A bird arrives carrying a parcel: "
            );
            t.sendMessage(ChatColor.GREEN + message);
            t.sendMessage("" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "Signed " + playerName);
        }
    }
}
