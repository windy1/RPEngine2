package tv.twitch.moonmoon.rpengine2.cmd.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ColorListCommand implements CommandExecutor {

    private static final String HEADER = ChatColor.BLUE + "Color List: ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(HEADER);
        for (ChatColor color : ChatColor.values()) {
            sender.sendMessage(ChatColor.BLUE + "# " + color + color.name().toLowerCase());
        }
        return true;
    }
}
