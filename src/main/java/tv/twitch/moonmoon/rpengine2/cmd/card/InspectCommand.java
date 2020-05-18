package tv.twitch.moonmoon.rpengine2.cmd.card;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class InspectCommand extends AbstractCoreCommandExecutor {

    private final RpPlayerRepo playerRepo;
    private final AttributeRepo attributeRepo;
    private final SelectRepo selectRepo;

    @Inject
    public InspectCommand(
        Plugin plugin,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo
    ) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        Player mcPlayer = (Player) sender;
        Player mcTarget = Bukkit.getPlayer(args[0]);
        Result<RpPlayer> t = playerRepo.getPlayer(mcTarget);
        RpPlayer target;
        int range = plugin.getConfig().getInt("inspectRange", 0);

        Optional<String> err = t.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }
        target = t.get();

        if (mcTarget == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        if (range > 0 && mcPlayer.getLocation().distance(mcTarget.getLocation()) > range) {
            sender.sendMessage(
                "" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC
                    + "You squint and try to make out the shadowy figure; " +
                    "alas, they are too far away."
            );
            return true;
        }

        new Card(playerRepo, attributeRepo, selectRepo, target, true).sendTo(sender);

        return true;
    }

    @Override
    public String getConfigPath() {
        return "commands.inspect";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
