package tv.twitch.moonmoon.rpengine2.spigot.cmd.help;

import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandUsage {

    private final String base;
    private final List<ArgumentLabel> args;

    public CommandUsage(String base, List<ArgumentLabel> args) {
        this.base = Objects.requireNonNull(base);
        this.args = Objects.requireNonNull(args);
    }

    public CommandUsage(String base) {
        this(base, Collections.emptyList());
    }

    public String toString() {
        return ChatColor.BLUE + "# " + ChatColor.GREEN + base + " " +
            args.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}
