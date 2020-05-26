package tv.twitch.moonmoon.rpengine2.spigot.data.player;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.player.AbstractRpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.spigot.model.player.SpigotRpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class SpigotRpPlayerDbo extends AbstractRpPlayerDbo {

    private final Plugin plugin;

    @Inject
    public SpigotRpPlayerDbo(Plugin plugin, RpDb db) {
        super(db, plugin.getLogger());
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void insertPlayerAttributeAsync(
            int playerId,
            int attributeId,
            Object value,
            Callback<Void> callback
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(insertPlayerAttributeAsync(playerId, attributeId, value))
        );
    }

    @Override
    public void updatePlayerAttributeAsync(
            int playerId,
            int attributeId,
            Object value,
            Callback<Void> callback
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(updatePlayerAttribute(playerId, attributeId, value))
        );
    }

    @Override
    public void deletePlayerAttributesAsync(int attributeId, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(deletePlayerAttributes(attributeId))
        );
    }

    @Override
    public void setSessionAsync(int playerId, Instant sessionStart, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(setSession(playerId, sessionStart))
        );
    }

    @Override
    protected RpPlayer readRpPlayer(ResultSet results) throws SQLException {
        int playerId = results.getInt("id");
        Set<RpPlayerAttribute> attributes;

        Result<Set<RpPlayerAttribute>> attributesResult = selectPlayerAttributes(playerId);

        Optional<String> err = attributesResult.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            attributes = new HashSet<>();
        } else {
            attributes = attributesResult.get();
        }

        return new SpigotRpPlayer(
            playerId,
            Instant.parse(results.getString("created")),
            results.getString("username"),
            UUID.fromString(results.getString("uuid")),
            attributes,
            Duration.ofSeconds(results.getInt("played")),
            Optional.ofNullable(results.getString("session_start"))
                .map(Instant::parse)
                .orElse(null)
        );
    }
}
