package tv.twitch.moonmoon.rpengine2.sponge.duel.data;

import org.spongepowered.api.Sponge;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.duel.data.AbstractDuelConfigDbo;
import tv.twitch.moonmoon.rpengine2.sponge.RpEngine2;
import tv.twitch.moonmoon.rpengine2.util.Callback;

import javax.inject.Inject;
import java.util.Objects;

public class SpongeDuelConfigDbo extends AbstractDuelConfigDbo {

    private final RpEngine2 plugin;

    @Inject
    public SpongeDuelConfigDbo(RpDb db, RpEngine2 plugin) {
        super(db);
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void setReadRulesAsync(int playerId, Callback<Void> callback) {
        Sponge.getScheduler().createAsyncExecutor(plugin).execute(() ->
            callback.accept(setReadRules(playerId))
        );
    }
}
