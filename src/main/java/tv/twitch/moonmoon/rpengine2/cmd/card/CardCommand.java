package tv.twitch.moonmoon.rpengine2.cmd.card;

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

public class CardCommand extends AbstractCoreCommandExecutor {

    private final RpPlayerRepo playerRepo;
    private final SelectRepo selectRepo;
    private final AttributeRepo attributeRepo;

    @Inject
    public CardCommand(
        Plugin plugin,
        RpPlayerRepo playerRepo,
        SelectRepo selectRepo,
        AttributeRepo attributeRepo
    ) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        Result<RpPlayer> p = playerRepo.getPlayer((Player) sender);
        RpPlayer player;

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }
        player = p.get();

        new Card(playerRepo, attributeRepo, selectRepo, player, false).sendTo(sender);

        return true;
    }

    @Override
    public String getConfigPath() {
        return "commands.card";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
