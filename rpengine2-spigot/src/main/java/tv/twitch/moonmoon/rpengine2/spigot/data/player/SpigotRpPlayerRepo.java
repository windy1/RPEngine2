package tv.twitch.moonmoon.rpengine2.spigot.data.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.AbstractRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.spigot.model.select.SpigotOption;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;

import java.util.*;
import java.util.logging.Logger;

@Singleton
public class SpigotRpPlayerRepo extends AbstractRpPlayerRepo {

    private final Logger log;

    @Inject
    public SpigotRpPlayerRepo(
        RpPlayerDbo playerDbo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        @PluginLogger Logger log
    ) {
        super(playerDbo, attributeRepo, selectRepo);
        this.log = log;
    }


    /**
     * Returns the player's `identity`, or, `display name` within the plugin
     *
     * @param player Player to get identity of
     * @return Player identity
     */
    public String getIdentity(RpPlayer player) {
        StringBuilder ident = new StringBuilder(getPrefix(player));
        String title = getTitle(player);
        if (!title.equals("")) {
            ident.append(title);
            ident.append(" ");
        }
        ident.append(getIdentityPlain(player));

        return ident.toString();
    }

    /**
     * Returns the player's prefix as marked by `/rpengine at setmarker {attribute}`
     *
     * @param player Player
     * @return Player prefix
     */
    public String getPrefix(RpPlayer player) {
        return getMarkerColor(player)
            .map(c -> net.md_5.bungee.api.ChatColor.valueOf(c.name()))
            .map(net.md_5.bungee.api.ChatColor::toString)
            .orElse("");
    }

    @Override
    protected Optional<String> getPlayerName(UUID playerId) {
        return Optional.ofNullable(Bukkit.getOfflinePlayer(playerId).getName());
    }

    @Override
    public void onWarning(String message) {
        log.warning(message);
    }

    private Optional<ChatColor> getMarkerColor(RpPlayer player) {
        return getMarkerOption(player)
            .map(s -> (SpigotOption) s)
            .flatMap(SpigotOption::getColor);
    }
}
