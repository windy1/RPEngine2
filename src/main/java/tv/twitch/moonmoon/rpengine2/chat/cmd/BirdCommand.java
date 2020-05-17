package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class BirdCommand implements CommandExecutor {

    @Inject
    public BirdCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
