package tv.twitch.moonmoon.rpengine2.cmd.card;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
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

    @Inject
    public CardCommand(RpPlayerRepo playerRepo, SelectRepo selectRepo) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        Result<RpPlayer> player = playerRepo.getPlayer((Player) sender);

        Optional<String> err = player.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }

        sender.sendMessage(String.format(HEADER, sender.getName()));
        sender.sendMessage(SUB_HEADER);

        player.get().getAttributes().stream()
            .map(a -> AttributeLabel.from(a, selectRepo))
            .forEach(a -> sender.spigot().sendMessage(a.toTextComponent()));

        return true;
    }
}
