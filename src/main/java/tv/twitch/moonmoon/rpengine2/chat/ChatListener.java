package tv.twitch.moonmoon.rpengine2.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.di.PluginLogger;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class ChatListener implements Listener {

    private final RpPlayerRepo playerRepo;
    private final Chat chat;
    private final Logger log;

    @Inject
    public ChatListener(RpPlayerRepo playerRepo, Chat chat, @PluginLogger Logger log) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.chat = Objects.requireNonNull(chat);
        this.log = Objects.requireNonNull(log);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Result<RpPlayer> p = playerRepo.getPlayer(e.getPlayer());

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            log.warning(err.get());
        } else {
            chat.handlePlayerJoined(p.get());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player mcPlayer = e.getPlayer();
        Result<RpPlayer> p = playerRepo.getPlayer(mcPlayer);

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            return;
        }

        e.setCancelled(chat.sendMessage(p.get(), e.getMessage()));
    }
}
