package tv.twitch.moonmoon.rpengine2.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatConfig;
import tv.twitch.moonmoon.rpengine2.cmd.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.cmd.parser.CommandParser;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class CommandChatConfigParser implements CommandParser<ChatConfig> {

    private final CommandPlayerParser playerParser;
    private final ChatConfigRepo configRepo;

    @Inject
    public CommandChatConfigParser(CommandPlayerParser playerParser, ChatConfigRepo configRepo) {
        this.playerParser = Objects.requireNonNull(playerParser);
        this.configRepo = Objects.requireNonNull(configRepo);
    }

    @Override
    public Optional<ChatConfig> parse(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return Optional.empty();
        }

        RpPlayer player = playerParser.parse(sender).orElse(null);
        Result<ChatConfig> c;

        if (player == null) {
            return Optional.empty();
        }

        c = configRepo.getConfig(player);

        Optional<String> err = c.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return Optional.empty();
        }

        return Optional.of(c.get());
    }
}
