package tv.twitch.moonmoon.rpengine2.spigot.util;

import org.bukkit.ChatColor;

import java.util.Optional;

public class SpigotUtils {

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
