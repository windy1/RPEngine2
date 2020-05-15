package tv.twitch.moonmoon.rpengine2.data.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;
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

    void load();

    void handlePlayerJoined(OfflinePlayer player);

    void flushJoinedPlayers();
}
