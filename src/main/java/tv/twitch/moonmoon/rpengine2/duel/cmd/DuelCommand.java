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
import tv.twitch.moonmoon.rpengine2.duel.cmd.parser.CommandDuelConfigParser;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;
import java.util.*;

public class DuelCommand extends AbstractCoreCommandExecutor {

    private final RpPlayerRepo playerRepo;
    private final CommandPlayerParser playerParser;
    private final CommandDuelConfigParser configParser;
    private final Duels duels;
    private final Map<UUID, Set<UUID>> invites = new HashMap<>();

    @Inject
    public DuelCommand(
        Plugin plugin,
        RpPlayerRepo playerRepo,
        CommandPlayerParser playerParser,
        CommandDuelConfigParser configParser, Duels duels
    ) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.playerParser = Objects.requireNonNull(playerParser);
        this.configParser = Objects.requireNonNull(configParser);
        this.duels = Objects.requireNonNull(duels);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        int maxRange = plugin.getConfig().getInt("duels.startRange", 10);
        Player mcPlayer = (Player) sender;
        DuelConfig config = configParser.parse(mcPlayer).orElse(null);
        RpPlayer player = playerParser.parse(sender).orElse(null);
        RpPlayer target;
        String playerIdent;
        String targetName = args[0];
        Player mcTarget = Bukkit.getPlayer(targetName);
        UUID playerId;
        UUID targetId;

        if (config == null) {
            return true;
        }

        if (!config.hasReadRules()) {
            sender.sendMessage(
                ChatColor.RED + "You must first consult the rules of dueling before " +
                    "participating (/duelrules)"
            );
            return true;
        }

        if (player == null) {
            return true;
        }

        if (mcTarget == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        if (mcPlayer.getLocation().distance(mcTarget.getLocation()) > maxRange) {
            sender.sendMessage(
                ChatColor.RED + "You must be within " + maxRange +
                    " blocks of a player to duel them"
            );
            return true;
        }

        target = playerParser.parse(mcTarget).orElse(null);

        if (target == null) {
            return true;
        }

        playerIdent = playerRepo.getIdentity(player);
        playerId = mcPlayer.getUniqueId();
        targetId = mcTarget.getUniqueId();
        boolean isResponse = getInvites(playerId).contains(targetId);

        // TODO: expire invites
        // TODO: cancel invites

        if (isResponse) {
            System.out.println("DEBUG1");
            duels.startDuel(player, target);
        } else {
            getInvites(targetId).add(playerId);

            mcTarget.sendMessage(
                ChatColor.DARK_RED + playerIdent + ChatColor.DARK_RED +
                    " has invited you to duel. Run " + ChatColor.GRAY + ChatColor.ITALIC
                    + "`/duel " + mcPlayer.getName() + "`" + ChatColor.RESET + ChatColor.DARK_RED
                    + " to accept"
            );

            mcPlayer.sendMessage(
                ChatColor.GREEN + "Invited " + playerRepo.getIdentity(target) + ChatColor.GREEN
                    + " to duel"
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
