package tv.twitch.moonmoon.rpengine2.spigot.duel.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.spigot.data.player.SpigotRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.spigot.model.player.SpigotRpPlayer;

import javax.inject.Inject;
import java.util.Objects;

public class DuelDeclineCommand extends AbstractCoreCommandExecutor {

    private final DuelInvites invites;
    private final CommandPlayerParser playerParser;
    private final SpigotRpPlayerRepo playerRepo;

    @Inject
    public DuelDeclineCommand(
        Plugin plugin,
        DuelInvites invites,
        CommandPlayerParser playerParser,
        RpPlayerRepo playerRepo
    ) {
        super(plugin);
        this.invites = Objects.requireNonNull(invites);
        this.playerParser = Objects.requireNonNull(playerParser);
        this.playerRepo = (SpigotRpPlayerRepo) Objects.requireNonNull(playerRepo);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        Player mcPlayer = (Player) sender;
        String targetName = args[0];
        Player mcTarget = Bukkit.getPlayer(targetName);
        RpPlayer player = playerParser.parse(sender).orElse(null);

        if (player == null) {
            return true;
        }

        if (mcTarget == null) {
            sender.sendMessage(
                ChatColor.RED + "Player not found (pending duels are cancelled on quit)"
            );
            return true;
        }

        if (invites.decline(mcPlayer.getUniqueId(), mcTarget.getUniqueId())) {
            mcPlayer.sendMessage(ChatColor.GREEN + "Duel declined");
            mcTarget.sendMessage(
                playerRepo.getIdentity(player) + ChatColor.GOLD
                    + " has declined your request to duel"
            );
        } else {
            mcPlayer.sendMessage(ChatColor.RED + "This player has not challenged you to a duel");
        }

        return true;
    }

    @Override
    public String getConfigPath() {
        return "duels.commands.dueldecline";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
