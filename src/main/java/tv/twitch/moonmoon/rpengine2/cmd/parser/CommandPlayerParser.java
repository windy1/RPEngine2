package tv.twitch.moonmoon.rpengine2.cmd.parser;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class CommandPlayerParser implements CommandParser<RpPlayer> {

    private final RpPlayerRepo playerRepo;

    @Inject
    public CommandPlayerParser(RpPlayerRepo playerRepo) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
    }

    @Override
    public Optional<RpPlayer> parse(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return Optional.empty();
        }

        Player mcPlayer = (Player) sender;
        Result<RpPlayer> p = playerRepo.getPlayer(mcPlayer);

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return Optional.empty();
        }
        return Optional.of(p.get());
    }
}
