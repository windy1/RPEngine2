package tv.twitch.moonmoon.rpengine2.chat.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class CountdownCommand implements CommandExecutor {

    private final Plugin plugin;

    @Inject
    public CountdownCommand(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + StringUtils.MUST_BE_PLAYER);
            return true;
        }

        FileConfiguration config = plugin.getConfig();
        int range = config.getInt("chat.countdown.range", 0);
        int timeSecs = 3;
        float volume = (float) config.getDouble("chat.countdown.volume", 0.5);
        float pitch = (float) config.getDouble("chat.countdown.pitch", 1);
        float goVolume = (float) config.getDouble("chat.countdown.goVolume", 0.5);
        float goPitch = (float) config.getDouble("chat.countdown.goPitch", 1);
        Location playerLocation = ((Player) sender).getLocation();

        String sound = config.getString(
            "chat.countdown.sound", "minecraft:ui.button.click"
        );

        String goSound = config.getString(
            "chat.countdown.sound", "minecraft:block.bell.use"
        );

        ChatColor color = StringUtils.getChatColor(
            config.getString("chat.countdown.color", "")
        ).orElse(ChatColor.WHITE);

        ChatColor goColor = StringUtils.getChatColor(
            config.getString("chat.countdown.goColor", "")
        ).orElse(ChatColor.WHITE);

        if (args.length > 0) {
            try {
                timeSecs = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number argument");
                return false;
            }
        }

        if (timeSecs <= 0) {
            sender.sendMessage("Time must be greater than or equal to 1");
            return true;
        }

        Set<UUID> playerIds = Bukkit.getOnlinePlayers().stream()
            .filter(p -> range == 0 || p.getLocation().distance(playerLocation) <= range)
            .map(Player::getUniqueId)
            .collect(Collectors.toSet());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Countdown(
            playerIds, color, goColor, sound, volume, pitch, goSound, goVolume, goPitch, timeSecs
        ), 0, 1000);

        return true;
    }

    static class Countdown extends TimerTask {

        final Set<UUID> playerIds;
        final ChatColor color;
        final ChatColor goColor;
        final String sound;
        final float volume;
        final float pitch;
        final String goSound;
        final float goVolume;
        final float goPitch;
        int timeSecs;

        Countdown(
            Set<UUID> playerIds,
            ChatColor color,
            ChatColor goColor,
            String sound,
            float volume,
            float pitch,
            String goSound,
            float goVolume,
            float goPitch,
            int timeSecs
        ) {
            this.playerIds = Objects.requireNonNull(playerIds);
            this.color = Objects.requireNonNull(color);
            this.goColor = Objects.requireNonNull(goColor);
            this.sound = Objects.requireNonNull(sound);
            this.volume = volume;
            this.pitch = pitch;
            this.goSound = Objects.requireNonNull(goSound);
            this.goVolume = goVolume;
            this.goPitch = goPitch;
            this.timeSecs = timeSecs;
        }

        @Override
        public void run() {
            List<Player> players = getPlayers();
            if (timeSecs == 0) {
                for (Player player : players) {
                    player.playSound(player.getLocation(), goSound, goVolume, goPitch);
                    player.sendTitle("" + goColor + ChatColor.BOLD + "Go!",
                        null, 0, 10, 5
                    );
                }

                cancel();
                return;
            }

            for (Player player : players) {
                player.playSound(player.getLocation(), sound, volume, pitch);
                player.sendTitle(
                    "" + color + ChatColor.BOLD + timeSecs,
                    null, 0, 10, 5
                );
            }

            timeSecs--;
        }

        private List<Player> getPlayers() {
            return playerIds.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }
    }
}
