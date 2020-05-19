package tv.twitch.moonmoon.rpengine2.duel.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import javax.inject.Inject;
import java.util.Objects;

public class ForfeitCommand extends AbstractCoreCommandExecutor {

    private final Duels duels;
    private final CommandPlayerParser playerParser;

    @Inject
    public ForfeitCommand(Plugin plugin, Duels duels, CommandPlayerParser playerParser) {
        super(plugin);
        this.duels = Objects.requireNonNull(duels);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        RpPlayer player = playerParser.parse(sender).orElse(null);

        if (player == null) {
            return true;
        }

        duels.forfeitDuel(player);

        return true;
    }

    @Override
    public String getConfigPath() {
        return "duels.commands.forfeit";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
