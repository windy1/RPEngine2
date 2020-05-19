package tv.twitch.moonmoon.rpengine2.duel.cmd;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.cmd.parser.CommandDuelConfigParser;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;
import java.util.Objects;
import java.util.UUID;

public class DuelCommand extends AbstractCoreCommandExecutor {

    private final RpPlayerRepo playerRepo;
    private final CommandPlayerParser playerParser;
    private final CommandDuelConfigParser configParser;
    private final Duels duels;
    private final DuelInvites invites;

    @Inject
    public DuelCommand(
        Plugin plugin,
        RpPlayerRepo playerRepo,
        CommandPlayerParser playerParser,
        CommandDuelConfigParser configParser,
        Duels duels,
        DuelInvites invites
    ) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.playerParser = Objects.requireNonNull(playerParser);
        this.configParser = Objects.requireNonNull(configParser);
        this.duels = Objects.requireNonNull(duels);
        this.invites = Objects.requireNonNull(invites);
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

        if (targetName.equals(mcPlayer.getName())) {
            sender.sendMessage(ChatColor.RED + "You cannot duel yourself");
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

        if (invites.has(playerId, targetId)) {
            invites.clear(playerId);
            invites.clear(targetId);
            duels.startDuel(player, target);
            return true;
        }

        invites.add(targetId, playerId);

        mcTarget.sendMessage(
            ChatColor.DARK_RED + playerIdent + ChatColor.DARK_RED +
                " has challenged you to a duel"
        );

        TextComponent reply = new TextComponent();
        TextComponent accept = new TextComponent("[Accept]");
        TextComponent decline = new TextComponent("[Decline]");

        accept.setColor(net.md_5.bungee.api.ChatColor.DARK_GREEN);
        accept.setBold(true);
        accept.setClickEvent(new ClickEvent(
            ClickEvent.Action.RUN_COMMAND, "/duel " + mcPlayer.getName())
        );

        decline.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        decline.setBold(true);
        decline.setClickEvent(new ClickEvent(
            ClickEvent.Action.RUN_COMMAND, "/dueldecline " + mcPlayer.getName())
        );

        reply.addExtra(accept);
        reply.addExtra(" ");
        reply.addExtra(decline);

        mcTarget.spigot().sendMessage(reply);

        mcPlayer.sendMessage(
            ChatColor.GREEN + "Challenged " + playerRepo.getIdentity(target) + ChatColor.GREEN
                + " to a duel"
        );

        return true;
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
