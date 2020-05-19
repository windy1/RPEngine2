package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.Objects;
import java.util.UUID;

public class BirdCommand extends AbstractCoreCommandExecutor {

    private final Chat chat;
    private final RpPlayerRepo playerRepo;
    private final CommandPlayerParser playerParser;

    @Inject
    public BirdCommand(Plugin plugin, Chat chat, RpPlayerRepo playerRepo, CommandPlayerParser playerParser) {
        super(plugin);
        this.chat = Objects.requireNonNull(chat);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String targetName = args[0];
        String message = String.join(" ", StringUtils.splice(args, 1));
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(targetName);
        RpPlayer p = playerParser.parse(player).orElse(null);
        PlayerInventory inv = player.getInventory();
        double distance;
        int tripTimeTicks;
        UUID targetId;

        if (p == null) {
            return true;
        }

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot send a bird to yourself");
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

        distance = player.getLocation().distance(target.getLocation());
        tripTimeTicks = (int) Math.round(distance / chat.getBirdSpeed() * 20);
        targetId = target.getUniqueId();

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                receiveBird(playerRepo.getIdentity(p), targetId, message),
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

    @Override
    public String getConfigPath() {
        return "chat.commands.bird";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
