package tv.twitch.moonmoon.rpengine2.cmd.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.model.select.Select;

import java.util.ArrayList;
import java.util.Objects;

public class SelectLabel {

    private final String selectName;
    private final String[] options;

    public SelectLabel(String selectName, String[] options) {
        this.selectName = Objects.requireNonNull(selectName);
        this.options = Objects.requireNonNull(options);
    }

    public static SelectLabel from(Select select) {
        String header = ChatColor.BLUE + "# " + ChatColor.GREEN + select.getName();
        String[] options = new ArrayList<>(select.getOptions()).stream()
            .map(o ->
                ChatColor.BLUE + "# " + ChatColor.DARK_GRAY + "  - "
                    + o.getColor().orElse(ChatColor.WHITE) + o.getDisplay()
            )
            .toArray(String[]::new);

        return new SelectLabel(header, options);
    }

    public void sendTo(CommandSender sender) {
        sender.sendMessage(selectName);
        sender.sendMessage(options);
    }
}
