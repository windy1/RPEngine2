package tv.twitch.moonmoon.rpengine2.spigot.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class CommandExecution implements Runnable {

    private final UUID playerId;
    private final String command;

    public CommandExecution(UUID playerId, String command) {
        this.playerId = Objects.requireNonNull(playerId);
        this.command = Objects.requireNonNull(command);
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            player.performCommand(command);
        }
    }
}
