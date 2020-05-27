package tv.twitch.moonmoon.rpengine2.sponge.data.player;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.AbstractRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.sponge.model.select.SpongeOption;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class SpongeRpPlayerRepo extends AbstractRpPlayerRepo {

    private final Logger log;

    @Inject
    public SpongeRpPlayerRepo(
        RpPlayerDbo playerDbo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        Logger log
    ) {
        super(playerDbo, attributeRepo, selectRepo);
        this.log = Objects.requireNonNull(log);
    }

    public Text getIdentity(RpPlayer player) {
        Text.Builder ident = Text.builder().color(getPrefix(player));
        String title = getTitle(player);
        if (!title.equals("")) {
            ident.append(Text.of(title));
            ident.append(Text.of(" "));
        }
        ident.append(Text.of(getIdentityPlain(player)));

        return ident.build();
    }

    public TextColor getPrefix(RpPlayer player) {
        return getMarkerOption(player)
            .map(s -> (SpongeOption) s)
            .flatMap(SpongeOption::getColor)
            .orElse(TextColors.WHITE);
    }

    @Override
    protected Optional<String> getPlayerName(UUID playerId) {
        return Sponge.getServer().getPlayer(playerId)
            .map(Player::getName);
    }

    @Override
    public void onWarning(String message) {
        log.warn(message);
    }
}
