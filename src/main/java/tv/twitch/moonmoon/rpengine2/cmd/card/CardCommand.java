package tv.twitch.moonmoon.rpengine2.cmd.card;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class CardCommand implements CommandExecutor {

    private static final String HEADER =
        ChatColor.BLUE + "========== " + ChatColor.GREEN + "%s " + ChatColor.BLUE + "==========";

    private static final String SUB_HEADER =
        "" + ChatColor.GRAY + ChatColor.ITALIC + "Click a line to edit";

    private final RpPlayerRepo playerRepo;
    private final SelectRepo selectRepo;
    private final AttributeRepo attributeRepo;

    @Inject
    public CardCommand(
        RpPlayerRepo playerRepo,
        SelectRepo selectRepo,
        AttributeRepo attributeRepo
    ) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        Result<RpPlayer> p = playerRepo.getPlayer((Player) sender);
        RpPlayer player;

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }
        player = p.get();

        sender.sendMessage(String.format(HEADER, playerRepo.getIdentity(player)));
        sender.sendMessage(SUB_HEADER);

        player.getAttributes().stream()
            .map(a -> {
                //noinspection OptionalGetWithoutIsPresent
                Attribute attribute = attributeRepo.getAttribute(a.getName()).get();
                return AttributeLabel.from(
                    a,
                    attribute.getDisplay(),
                    attribute.getFormatString().orElse(null),
                    selectRepo
                );
            })
            .forEach(a -> sender.spigot().sendMessage(a.toTextComponent()));

        return true;
    }
}
