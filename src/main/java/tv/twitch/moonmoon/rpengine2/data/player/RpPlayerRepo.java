package tv.twitch.moonmoon.rpengine2.data.player;

import org.bukkit.OfflinePlayer;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

public interface RpPlayerRepo extends Repo {

    Set<RpPlayer> getPlayers();

    Result<RpPlayer> getPlayer(OfflinePlayer player);

    Optional<RpPlayer> getLoadedPlayer(String name);

    String getIdentity(RpPlayer player);

    String getIdentityPlain(RpPlayer player);

    String getPrefix(RpPlayer player);

    String getTitle(RpPlayer player);

    void setAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Callback<Void> callback
    );

    void removeAttributesAsync(int attributeId, Callback<Void> callback);

    void startSessionAsync(RpPlayer player);

    void clearSession(RpPlayer player);

    void setPlayed(RpPlayer player, Duration duration);

    Result<Void> reloadPlayers();

    void shutdown();

}
