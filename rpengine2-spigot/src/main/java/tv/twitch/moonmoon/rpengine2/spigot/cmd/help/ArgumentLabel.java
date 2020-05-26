package tv.twitch.moonmoon.rpengine2.spigot.cmd.help;

import org.bukkit.ChatColor;

public class ArgumentLabel {

    private final String text;
    private final boolean required;

    public ArgumentLabel(String text, boolean required) {
        this.text = text;
        this.required = required;
    }

    @Override
    public String toString() {
        return "" + ChatColor.DARK_GRAY +
            (required ? '<' : '[') +
            ChatColor.WHITE +
            text +
            ChatColor.DARK_GRAY +
            (required ? '>' : ']') +
            ChatColor.RESET;
    }
}
