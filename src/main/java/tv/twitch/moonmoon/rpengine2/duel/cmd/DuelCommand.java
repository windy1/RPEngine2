package tv.twitch.moonmoon.rpengine2.duel.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.cmd.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;
import java.util.Objects;

public class DuelCommand extends AbstractCoreCommandExecutor {

    private final RpPlayerRepo playerRepo;
    private final CommandPlayerParser playerParser;

    @Inject
    public DuelCommand(Plugin plugin, RpPlayerRepo playerRepo, CommandPlayerParser playerParser) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        RpPlayer player = playerParser.parse(sender).orElse(null);

        if (player == null) {
            return true;
        }

        return false;
    }

    @Override
    public String getConfigPath() {
        return "duels.commands.duel";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
