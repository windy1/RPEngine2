package tv.twitch.moonmoon.rpengine2.data.player;

import org.bukkit.OfflinePlayer;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface RpPlayerRepo extends Repo {

    Set<RpPlayer> getPlayers();

    Result<RpPlayer> getPlayer(OfflinePlayer player);

    Optional<RpPlayer> getPlayer(String name);

    void setAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Consumer<Result<Void>> callback
    );

    void removeAttributesAsync(int attributeId, Consumer<Result<Void>> callback);

    Result<Void> reloadPlayers();

    void handlePlayerJoined(OfflinePlayer player);

    void flushJoinedPlayers();
}
