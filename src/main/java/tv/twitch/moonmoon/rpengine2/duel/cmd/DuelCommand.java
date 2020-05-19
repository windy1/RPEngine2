package tv.twitch.moonmoon.rpengine2.duel.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;
import java.util.*;

public class DuelCommand extends AbstractCoreCommandExecutor {

    private final RpPlayerRepo playerRepo;
    private final CommandPlayerParser playerParser;
    private final Duels duels;
    private final Map<UUID, Set<UUID>> invites = new HashMap<>();

    @Inject
    public DuelCommand(
        Plugin plugin,
        RpPlayerRepo playerRepo,
        CommandPlayerParser playerParser,
        Duels duels
    ) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.playerParser = Objects.requireNonNull(playerParser);
        this.duels = Objects.requireNonNull(duels);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        System.out.println("DEBUG1");
        if (args.length == 0) {
            return false;
        }

        System.out.println("DEBUG2");

        int maxRange = plugin.getConfig().getInt("duels.startRange", 10);
        Player mcPlayer = (Player) sender;
        RpPlayer player = playerParser.parse(sender).orElse(null);
        RpPlayer target;
        String playerIdent;
        String targetName = args[0];
        Player mcTarget = Bukkit.getPlayer(targetName);
        UUID playerId;
        UUID targetId;

        if (player == null) {
            return true;
        }

        System.out.println("DEBUG3");

        if (mcTarget == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        System.out.println("DEBUG4");

        if (mcPlayer.getLocation().distance(mcTarget.getLocation()) > maxRange) {
            sender.sendMessage(
                ChatColor.RED + "You must be within " + maxRange +
                    " blocks of a player to duel them"
            );
            return true;
        }

        System.out.println("DEBUG5");

        target = playerParser.parse(mcTarget).orElse(null);

        if (target == null) {
            return true;
        }

        playerIdent = playerRepo.getIdentity(player);
        playerId = mcPlayer.getUniqueId();
        targetId = mcTarget.getUniqueId();
        boolean isResponse = getInvites(playerId).contains(targetId);

        if (isResponse) {
            duels.startDuel(player, target);
        } else {
            getInvites(targetId).add(playerId);
            mcTarget.sendMessage(
                ChatColor.DARK_RED + playerIdent + ChatColor.DARK_RED +
                    " has invited you to duel. Run `/duel " + playerIdent + "` to accept"
            );
        }

        return true;
    }

    private Set<UUID> getInvites(UUID invitedPlayerId) {
        return invites.computeIfAbsent(invitedPlayerId, k -> new HashSet<>());
    }

    @Override
    public String getConfigPath() {
        return "duels.commands.duel";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
