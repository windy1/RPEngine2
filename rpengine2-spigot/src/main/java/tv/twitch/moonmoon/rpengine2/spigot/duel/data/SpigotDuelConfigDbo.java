package tv.twitch.moonmoon.rpengine2.spigot.duel.data;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.duel.data.AbstractDuelConfigDbo;
import tv.twitch.moonmoon.rpengine2.util.Callback;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotDuelConfigDbo extends AbstractDuelConfigDbo {

    private final Plugin plugin;

    @Inject
    public SpigotDuelConfigDbo(Plugin plugin, RpDb db) {
        super(db);
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void setReadRulesAsync(int playerId, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(setReadRules(playerId))
        );
    }
}
