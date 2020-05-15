package tv.twitch.moonmoon.rpengine2.cmd.card;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class CardSetCommand implements CommandExecutor {

    private static final List<CommandUsage> USAGES = new ArrayList<>();
    private static final Help HELP = new Help(
        ChatColor.BLUE + "Card Set Sub Commands: ", USAGES
    );

    static {
        USAGES.add(new CommandUsage("/cardset", Arrays.asList(
            new ArgumentLabel("name", true),
            new ArgumentLabel("value", true)
        )));
    }

    private final RpPlayerRepo playerRepo;
    private final AttributeRepo attributeRepo;

    @Inject
    public CardSetCommand(RpPlayerRepo playerRepo, AttributeRepo attributeRepo) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        if (args.length < 2) {
            return HELP.handle(sender, new String[] { "help" });
        }

        String name = args[0];
        String value = args[1];

        Optional<Attribute> a = attributeRepo.getAttribute(name);
        if (!a.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Unknown attribute");
            return true;
        }

        Result<RpPlayer> p = playerRepo.getPlayer((Player) sender);

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }

        playerRepo.setAttributeAsync(p.get(), a.get().getId(), value, r -> {
            Optional<String> setErr = r.getError();
            if (setErr.isPresent()) {
                sender.sendMessage(ChatColor.RED + setErr.get());
            } else {
                sender.sendMessage(ChatColor.GREEN + "Card updated");
            }
        });

        return true;
    }
}
