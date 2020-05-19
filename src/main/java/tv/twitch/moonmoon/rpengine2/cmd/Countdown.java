package tv.twitch.moonmoon.rpengine2.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Countdown extends TimerTask {

    private final Set<UUID> playerIds;
    private final ChatColor color;
    private final ChatColor goColor;
    private final String sound;
    private final float volume;
    private final float pitch;
    private final String goSound;
    private final float goVolume;
    private final float goPitch;
    private final Runnable callback;
    private int timeSecs;

    public Countdown(
        Set<UUID> playerIds,
        ChatColor color,
        ChatColor goColor,
        String sound,
        float volume,
        float pitch,
        String goSound,
        float goVolume,
        float goPitch,
        int timeSecs,
        Runnable callback
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
        this.callback = callback;
    }

    @Override
    public void run() {
        List<Player> players = getPlayers();
        if (timeSecs == 0) {
            for (Player player : players) {
                player.playSound(player.getLocation(), goSound, goVolume, goPitch);
                player.sendTitle("" + goColor + ChatColor.BOLD + "GO!",
                    null, 0, 10, 5
                );
            }

            cancel();

            if (callback != null) {
                callback.run();
            }
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

    public void start() {
        new Timer().scheduleAtFixedRate(this, 0, 1000);
    }

    public static Countdown from(
        FileConfiguration config,
        Set<UUID> playerIds,
        int timeSecs,
        Runnable callback
    ) {
        float volume = (float) config.getDouble("countdown.volume", 0.5);
        float pitch = (float) config.getDouble("countdown.pitch", 1);
        float goVolume = (float) config.getDouble("countdown.goVolume", 0.5);
        float goPitch = (float) config.getDouble("countdown.goPitch", 1);

        String sound = config.getString(
            "countdown.sound", "minecraft:ui.button.click"
        );

        String goSound = config.getString(
            "countdown.goSound", "minecraft:block.bell.use"
        );

        ChatColor color = StringUtils.getChatColor(
            config.getString("countdown.color", "")
        ).orElse(ChatColor.WHITE);

        ChatColor goColor = StringUtils.getChatColor(
            config.getString("countdown.goColor", "")
        ).orElse(ChatColor.WHITE);

        return new Countdown(
            playerIds,
            color,
            goColor,
            sound,
            volume,
            pitch,
            goSound,
            goVolume,
            goPitch,
            timeSecs,
            callback
        );
    }

    private List<Player> getPlayers() {
        return playerIds.stream()
            .map(Bukkit::getPlayer)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
