package tv.twitch.moonmoon.rpengine2.cmd.card;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.CommandDispatcher;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class CardSetCommand extends AbstractCoreCommandExecutor {

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
    private final CommandDispatcher commandDispatcher;
    private final CommandPlayerParser playerParser;

    @Inject
    public CardSetCommand(
        Plugin plugin,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        CommandDispatcher commandDispatcher,
        CommandPlayerParser playerParser
    ) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.commandDispatcher = Objects.requireNonNull(commandDispatcher);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return HELP.handle(sender, new String[] { "help" });
        }

        String name = args[0];
        String value = String.join(" ", StringUtils.splice(args, 1));
        Player mcPlayer = (Player) sender;
        RpPlayer player = playerParser.parse(mcPlayer).orElse(null);

        if (player == null) {
            return true;
        }

        Optional<Attribute> a = attributeRepo.getAttribute(name);
        if (!a.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Unknown attribute");
            return true;
        }

        playerRepo.setAttributeAsync(player, a.get().getId(), value, r -> {
            Optional<String> setErr = r.getError();
            if (setErr.isPresent()) {
                sender.sendMessage(ChatColor.RED + setErr.get());
            } else {
                commandDispatcher.add(mcPlayer.getUniqueId(), "card");
            }
        });

        return true;
    }

    @Override
    public String getConfigPath() {
        return "commands.cardset";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
