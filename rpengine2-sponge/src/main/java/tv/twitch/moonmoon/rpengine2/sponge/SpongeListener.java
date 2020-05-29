package tv.twitch.moonmoon.rpengine2.sponge;

import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import tv.twitch.moonmoon.rpengine2.Engine;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

public class SpongeListener {

    private final Engine engine;
    private final RpPlayerRepo playerRepo;
    private final Logger log;

    @Inject
    public SpongeListener(Engine engine, RpPlayerRepo playerRepo, @PluginLogger Logger log) {
        this.engine = Objects.requireNonNull(engine);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.log = Objects.requireNonNull(log);
    }

    @Listener(order = Order.PRE, beforeModifications = true)
    public void onPlayerJoin(ClientConnectionEvent.Join e) {
        getPlayer(e.getTargetEntity()).ifPresent(engine::handlePlayerJoined);
    }

    @Listener(order = Order.POST)
    public void onPlayerQuit(ClientConnectionEvent.Disconnect e) {
        getPlayer(e.getTargetEntity()).ifPresent(engine::handlePlayerQuit);
    }

    private Optional<RpPlayer> getPlayer(Player player) {
        Result<RpPlayer> p = playerRepo.getPlayer(player.getUniqueId());

        Optional<String> err = p.getError();
        if (err.isPresent()) {
            log.warn(err.get());
            return Optional.empty();
        }

        return Optional.of(p.get());
    }
}
