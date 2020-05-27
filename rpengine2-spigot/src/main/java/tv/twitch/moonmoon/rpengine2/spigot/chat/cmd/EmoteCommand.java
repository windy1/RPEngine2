package tv.twitch.moonmoon.rpengine2.spigot.chat.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.spigot.cmd.parser.CommandPlayerParser;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.spigot.data.player.SpigotRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.spigot.model.player.SpigotRpPlayer;

import javax.inject.Inject;
import java.util.Objects;

public class EmoteCommand extends AbstractCoreCommandExecutor {

    private final CommandPlayerParser playerParser;
    private final SpigotRpPlayerRepo playerRepo;

    @Inject
    public EmoteCommand(
        Plugin plugin,
        CommandPlayerParser playerParser,
        RpPlayerRepo playerRepo
    ) {
        super(plugin);
        this.playerParser = Objects.requireNonNull(playerParser);
        this.playerRepo = (SpigotRpPlayerRepo) Objects.requireNonNull(playerRepo);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        Player mcPlayer = (Player) sender;
        RpPlayer player = playerParser.parse(mcPlayer).orElse(null);
        int range = plugin.getConfig().getInt("chat.emoteRange", 0);

        TextComponent c = new TextComponent();
        TextComponent n = new TextComponent(playerRepo.getIdentity(player));
        TextComponent e = new TextComponent(String.join(" ", args));

        c.setItalic(true);
        e.setColor(ChatColor.YELLOW);

        c.addExtra(n);
        c.addExtra(" ");
        c.addExtra(e);

        if (player == null) {
            return true;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (range == 0 || p.getLocation().distance(mcPlayer.getLocation()) <= range) {
                p.spigot().sendMessage(c);
            }
        }

        return true;
    }

    @Override
    public String getConfigPath() {
        return "chat.commands.emote";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
