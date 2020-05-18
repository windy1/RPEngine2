package tv.twitch.moonmoon.rpengine2.util;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Optional;

public class StringUtils {

    public static final String GENERIC_ERROR =
        "An unexpected error occurred. See console for details";

    public static final String MUST_BE_PLAYER = "Only players may execute this command";

    public static String[] splice(String[] args, int start) {
        if (start > args.length - 1) {
            return new String[0];
        } else {
            return Arrays.copyOfRange(args, start, args.length);
        }
    }

    public static Optional<ChatColor> getChatColor(String name) {
        if (name == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(ChatColor.valueOf(name.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static Optional<net.md_5.bungee.api.ChatColor> getSpigotChatColor(String name) {
        return getChatColor(name).map(c -> net.md_5.bungee.api.ChatColor.valueOf(c.name()));
    }
}
