package tv.twitch.moonmoon.rpengine2.cmd.action;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.cmd.AbstractCoreCommandExecutor;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class PlayerActionMenuCommand extends AbstractCoreCommandExecutor {

    private static final String FOOTER = ChatColor.BLUE + "===============";

    private final RpPlayerRepo playerRepo;
    private final Chat chat;

    @Inject
    public PlayerActionMenuCommand(Plugin plugin, RpPlayerRepo playerRepo, Optional<Chat> chat) {
        super(plugin);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.chat = chat.orElse(null);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String targetName = args[0];
        Player mcTarget = Bukkit.getPlayer(targetName);
        Result<RpPlayer> t;
        RpPlayer target;

        if (mcTarget == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        t = playerRepo.getPlayer(mcTarget);

        Optional<String> err = t.getError();
        if (err.isPresent()) {
            sender.sendMessage(ChatColor.RED + err.get());
            return true;
        }
        target = t.get();

        StringBuilder header = new StringBuilder(
            " " + playerRepo.getIdentity(target) + " " + ChatColor.BLUE
        );

        int headerLength = 20;
        int padding = (headerLength - header.length()) / 2;

        for (int i = 0; i < padding; i++) {
            header.insert(0, '=');
            header.append('=');
        }
        header.insert(0, ChatColor.BLUE);

        sender.sendMessage(header.toString());

        sender.spigot().sendMessage(PlayerAction.inspect(targetName).toTextComponent());

        if (chat != null) {
            sender.spigot().sendMessage(PlayerAction.bird(targetName).toTextComponent());
        }

        sender.sendMessage(FOOTER);

        return true;
    }

    @Override
    public String getConfigPath() {
        return "commands.playeractionmenu";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
