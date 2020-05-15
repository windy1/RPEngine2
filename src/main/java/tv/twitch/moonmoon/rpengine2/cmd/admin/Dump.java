package tv.twitch.moonmoon.rpengine2.cmd.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;

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
    }

    private final RpPlayerRepo playerRepo;

    @Inject
    public Dump(RpPlayerRepo playerRepo) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return HELP.handle(sender, args);
        }

        if (args[0].equalsIgnoreCase("player")) {
            String name = args[1];

            Optional<RpPlayer> p = playerRepo.getPlayer(name);
            if (!p.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Player not found");
            } else {
                sender.sendMessage(p.get().toString());
            }

            return true;
        }

        return false;
    }
}
