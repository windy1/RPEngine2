package tv.twitch.moonmoon.rpengine2.spigot.duel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duel;
import tv.twitch.moonmoon.rpengine2.duel.DuelMessenger;
import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.data.player.SpigotRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.util.Lang;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotDuelMessenger implements DuelMessenger {

    private final SpigotRpPlayerRepo players;

    @Inject
    public SpigotDuelMessenger(RpPlayerRepo playerRepo) {
        players = (SpigotRpPlayerRepo) Objects.requireNonNull(playerRepo);
    }

    @Override
    public void broadcastDuelEnd(Dueler winner, Dueler loser) {
        String message =
            players.getIdentity(winner.getPlayer()) +
                ChatColor.GOLD +
                Lang.getString("duels.duelEnd1") +
                players.getIdentity(loser.getPlayer()) +
                ChatColor.GOLD +
                Lang.getString("duels.duelEnd2");

        Bukkit.broadcastMessage(message);
    }

    @Override
    public void broadcastDuelTimeout(Duel duel) {
        String message =
            ChatColor.GOLD +
                Lang.getString("duels.duelTimeout1") +
                players.getIdentity(duel.getPlayer1().getPlayer()) +
                ChatColor.GOLD +
                Lang.getString("duels.duelTimeout2") +
                players.getIdentity(duel.getPlayer2().getPlayer()) +
                ChatColor.GOLD +
                Lang.getString("duels.duelTimeout3");

        Bukkit.broadcastMessage(message);
    }

    @Override
    public void broadcastDuelForfeit(RpPlayer winner, RpPlayer loser) {
        String message =
            players.getIdentity(loser) +
                ChatColor.GOLD +
                Lang.getString("duels.forfeit1") +
                players.getIdentity(winner) +
                ChatColor.GOLD +
                Lang.getString("duels.forfeit2");

        Bukkit.broadcastMessage(message);
    }
}
