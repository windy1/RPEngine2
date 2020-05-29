package tv.twitch.moonmoon.rpengine2.spigot.cmd.card;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.ArgumentLabel;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.CommandUsage;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.help.Help;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.spigot.model.select.SpigotOption;
import tv.twitch.moonmoon.rpengine2.spigot.task.CommandExecution;
import tv.twitch.moonmoon.rpengine2.task.AsyncHandler;

import javax.inject.Inject;
import java.util.*;

public class CardSelectCommand extends AbstractCoreCommandExecutor {

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
    private final CommandPlayerParser playerParser;
    private final AsyncHandler handler;

    @Inject
    public CardSelectCommand(
        Plugin plugin,
        SelectRepo selectRepo,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        CommandPlayerParser playerParser,
        AsyncHandler handler
    ) {
        super(plugin);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.playerParser = Objects.requireNonNull(playerParser);
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            return HELP.handle(sender, new String[] { "help" });
        }

        String selectName = args[0];
        Optional<Select> s = selectRepo.getSelect(selectName);
        Player mcPlayer = (Player) sender;
        Optional<Attribute> a = attributeRepo.getAttribute(selectName);
        Attribute attribute;
        Select select;
        RpPlayer player = playerParser.parse(mcPlayer).orElse(null);
        int curOptionId;

        if (player == null) {
            return true;
        }

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
                .map(o -> (SpigotOption) o)
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

            handler.post(new CommandExecution(mcPlayer.getUniqueId(), "card"));
        });

        return true;
    }

    @Override
    public String getConfigPath() {
        return "commands.cardselect";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
