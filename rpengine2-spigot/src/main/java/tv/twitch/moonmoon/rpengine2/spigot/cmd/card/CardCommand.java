package tv.twitch.moonmoon.rpengine2.spigot.cmd.card;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.parser.CommandPlayerParser;

import javax.inject.Inject;
import java.util.Objects;

public class CardCommand extends AbstractCoreCommandExecutor {

    private final RpPlayerRepo playerRepo;
    private final SelectRepo selectRepo;
    private final AttributeRepo attributeRepo;
    private final CommandPlayerParser playerParser;

    @Inject
    public CardCommand(
        Plugin plugin,
        RpPlayerRepo playerRepo,
        SelectRepo selectRepo,
        AttributeRepo attributeRepo,
        CommandPlayerParser playerParser
    ) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        RpPlayer player = playerParser.parse(sender).orElse(null);

        if (player == null) {
            return true;
        }

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
