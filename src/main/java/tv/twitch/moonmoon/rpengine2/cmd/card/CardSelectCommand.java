package tv.twitch.moonmoon.rpengine2.cmd.card;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.CommandDispatcher;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class CardSelectCommand implements CommandExecutor {

    private static final List<CommandUsage> USAGES = new ArrayList<>();
    private static final Help HELP = new Help(
        ChatColor.BLUE + "Card Select Sub Commands: ", USAGES
    );

    static {
        USAGES.add(new CommandUsage("/cardselect", Arrays.asList(
            new ArgumentLabel("select_name", true),
            new ArgumentLabel("choice", false)
        )));
    }

    private final SelectRepo selectRepo;
    private final RpPlayerRepo playerRepo;
    private final AttributeRepo attributeRepo;
    private final CommandDispatcher commandDispatcher;

    @Inject
    public CardSelectCommand(
        SelectRepo selectRepo,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        CommandDispatcher commandDispatcher
    ) {
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.commandDispatcher = Objects.requireNonNull(commandDispatcher);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            return HELP.handle(sender, new String[] { "help" });
        }

        String selectName = args[0];
        Optional<Select> s = selectRepo.getSelect(selectName);
        Player mcPlayer = (Player) sender;
        Result<RpPlayer> p = playerRepo.getPlayer(mcPlayer);
        Optional<Attribute> a = attributeRepo.getAttribute(selectName);
        Attribute attribute;
        Select select;
        RpPlayer player;
        int curOptionId;

        // check player
        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }
        player = p.get();

        // check select
        if (!s.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Select not found");
            return true;
        }
        select = s.get();

        // check if select is an attribute
        if (!a.isPresent()) {
            String message = ChatColor.RED +
                "This select has not been added to the attributes (/rpengine attribute)";
            sender.sendMessage(message);
            return true;
        }
        attribute = a.get();

        curOptionId = player.getAttribute(attribute.getId())
            .flatMap(RpPlayerAttribute::getValue)
            .map(at -> (Integer) at)
            .orElse(0);

        if (args.length < 2) {
            // list options
            sender.sendMessage(ChatColor.BLUE + "Select " + selectName + ": ");

            select.getOptions().stream()
                .map(o -> new OptionLabel(
                    o.getDisplay(),
                    selectName,
                    o.getName(),
                    o.getColor().orElse(null),
                    o.getId() == curOptionId
                ))
                .map(OptionLabel::toTextComponent)
                .forEach(c -> sender.spigot().sendMessage(c));
            return true;
        }

        // set option
        Optional<Option> o = select.getOption(args[1]);
        Option option;

        if (!o.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Unknown option");
            return true;
        }
        option = o.get();

        playerRepo.setAttributeAsync(player, attribute.getId(), option.getId(), r -> {
            Optional<String> setErr = r.getError();
            if (setErr.isPresent()) {
                sender.sendMessage(ChatColor.RED + setErr.get());
                return;
            }

            commandDispatcher.add(mcPlayer.getUniqueId(), "card");
        });

        return true;
    }
}
