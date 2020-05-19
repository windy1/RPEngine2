package tv.twitch.moonmoon.rpengine2.cmd.parser;

import org.bukkit.command.CommandSender;

import java.util.Optional;

public interface CommandParser<T> {

    Optional<T> parse(CommandSender sender);
}
