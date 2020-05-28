package tv.twitch.moonmoon.rpengine2.sponge.duel;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duel;
import tv.twitch.moonmoon.rpengine2.duel.DuelMessenger;
import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.sponge.data.player.SpongeRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.util.Lang;

import javax.inject.Inject;
import java.util.Objects;

public class SpongeDuelMessenger implements DuelMessenger {

    private final SpongeRpPlayerRepo players;

    @Inject
    public SpongeDuelMessenger(RpPlayerRepo playerRepo) {
        players = (SpongeRpPlayerRepo) Objects.requireNonNull(playerRepo);
    }

    @Override
    public void broadcastDuelEnd(Dueler winner, Dueler loser) {
        Text message = Text.of(
            players.getIdentity(winner.getPlayer()),
            TextColors.GOLD,
            Lang.getString("duels.duelEnd1"),
            players.getIdentity(loser.getPlayer()),
            TextColors.GOLD,
            Lang.getString("duels.duelEnd2")
        );

        Sponge.getServer().getBroadcastChannel().send(message);
    }

    @Override
    public void broadcastDuelTimeout(Duel duel) {
        Text message = Text.of(
            TextColors.GOLD,
            Lang.getString("duels.duelTimeout1"),
            TextColors.GOLD,
            Lang.getString("duels.duelTimeout2"),
            players.getIdentity(duel.getPlayer2().getPlayer()),
            TextColors.GOLD,
            Lang.getString("duels.duelTimeout3")
        );

        Sponge.getServer().getBroadcastChannel().send(message);
    }

    @Override
    public void broadcastDuelForfeit(RpPlayer winner, RpPlayer loser) {
        Text message = Text.of(
            players.getIdentity(loser),
            TextColors.GOLD,
            Lang.getString("duels.forfeit1"),
            players.getIdentity(winner),
            TextColors.GOLD,
            Lang.getString("duels.forfeit2")
        );

        Sponge.getServer().getBroadcastChannel().send(message);
    }
}
