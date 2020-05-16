package tv.twitch.moonmoon.rpengine2.cmd.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class Dump {

    private static final List<CommandUsage> USAGES = new ArrayList<>();
    private static final Help HELP = new Help(
        ChatColor.BLUE + "Dump Sub Commands: ", USAGES
    );

    static {
        USAGES.add(new CommandUsage("player", Collections.singletonList(
            new ArgumentLabel("name", true)
        )));

        USAGES.add(new CommandUsage("players"));
        USAGES.add(new CommandUsage("attributes"));
        USAGES.add(new CommandUsage("selects"));
    }

    private final RpPlayerRepo playerRepo;
    private final AttributeRepo attributeRepo;
    private final SelectRepo selectRepo;

    @Inject
    public Dump(RpPlayerRepo playerRepo, AttributeRepo attributeRepo, SelectRepo selectRepo) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            return HELP.handle(sender, new String[] { "help" });
        }

        switch (args[0]) {
            case "player":
                return handleDumpPlayer(sender, StringUtils.splice(args, 1));
            case "players": {
                sender.sendMessage(playerRepo.getPlayers().toString());
                return true;
            }
            case "attributes": {
                sender.sendMessage(attributeRepo.getAttributes().toString());
                return true;
            }
            case "selects": {
                sender.sendMessage(selectRepo.getSelects().toString());
                return true;
            }
            default:
                return false;
        }
    }

    private boolean handleDumpPlayer(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        Optional<RpPlayer> p = playerRepo.getPlayer(name);
        if (!p.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Player not found");
        } else {
            sender.sendMessage(p.get().toString());
        }

        return true;
    }
}
