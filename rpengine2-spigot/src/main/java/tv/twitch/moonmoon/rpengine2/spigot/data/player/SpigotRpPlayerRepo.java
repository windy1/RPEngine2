package tv.twitch.moonmoon.rpengine2.spigot.data.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.AbstractRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.spigot.model.select.SpigotOption;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class SpigotRpPlayerRepo extends AbstractRpPlayerRepo {

    @Inject
    public SpigotRpPlayerRepo(
        RpPlayerDbo playerDbo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        @PluginLogger Logger log
    ) {
        super(playerDbo, attributeRepo, selectRepo, log);
    }

    @Override
    public String getPrefix(RpPlayer player) {
        return getMarkerColor(player)
            .map(c -> net.md_5.bungee.api.ChatColor.valueOf(c.name()))
            .map(net.md_5.bungee.api.ChatColor::toString)
            .orElse("");
    }

    @Override
    protected String getPlayerName(UUID playerId) {
        return Bukkit.getOfflinePlayer(playerId).getName();
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    private Optional<ChatColor> getMarkerColor(RpPlayer player) {
        return getMarkerOption(player)
            .map(s -> (SpigotOption) s)
            .flatMap(SpigotOption::getColor);
    }
}
