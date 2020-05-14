package tv.twitch.moonmoon.rpengine2.cmd;

import com.google.inject.Inject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.Objects;

public class RpCommand implements CommandExecutor {

    private final AttributeAdmin attributeAdmin;

    @Inject
    public RpCommand(AttributeAdmin attributeAdmin) {
        this.attributeAdmin = Objects.requireNonNull(attributeAdmin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO: better help pages

        if (args.length >= 1 && args[0].equalsIgnoreCase("attribute")) {
            return attributeAdmin.handle(sender, StringUtils.splice(args, 1));
        }

        return false;
    }
}
