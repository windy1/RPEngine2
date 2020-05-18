package tv.twitch.moonmoon.rpengine2.data.player;

import org.bukkit.OfflinePlayer;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Optional;
import java.util.Set;

public interface RpPlayerRepo extends Repo {

    Set<RpPlayer> getPlayers();

    Result<RpPlayer> getPlayer(OfflinePlayer player);

    Optional<RpPlayer> getPlayer(String name);

    String getIdentity(RpPlayer player);

    String getIdentityPlain(RpPlayer player);

    String getPrefix(RpPlayer player);

    void setAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Callback<Void> callback
    );

    void removeAttributesAsync(int attributeId, Callback<Void> callback);

    Result<Void> reloadPlayers();

    void setChatChannelAsync(RpPlayer player, ChatChannel channel, Callback<Void> callback);

}
