package tv.twitch.moonmoon.rpengine2.duel.cmd.parser;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandParser;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class CommandDuelConfigParser implements CommandParser<DuelConfig> {

    private final DuelConfigRepo configRepo;
    private final CommandPlayerParser playerParser;

    @Inject
    public CommandDuelConfigParser(DuelConfigRepo configRepo, CommandPlayerParser playerParser) {
        this.configRepo = Objects.requireNonNull(configRepo);
        this.playerParser = Objects.requireNonNull(playerParser);
    }

    @Override
    public Optional<DuelConfig> parse(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return Optional.empty();
        }

        RpPlayer player = playerParser.parse(sender).orElse(null);

        if (player == null) {
            return Optional.empty();
        }

        Result<DuelConfig> c = configRepo.getConfig(player);

        Optional<String> err = c.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return Optional.empty();
        }

        return Optional.of(c.get());
    }
}
