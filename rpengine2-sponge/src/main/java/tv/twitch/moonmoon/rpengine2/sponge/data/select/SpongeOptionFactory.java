package tv.twitch.moonmoon.rpengine2.sponge.data.select;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.OptionFactory;
import tv.twitch.moonmoon.rpengine2.sponge.model.select.SpongeOption;

import javax.inject.Inject;
import java.time.Instant;

public class SpongeOptionFactory implements OptionFactory {

    @Inject
    public SpongeOptionFactory() {
    }

    @Override
    public Option newInstance(
        int id,
        int selectId,
        Instant created,
        String name,
        String display,
        String color
    ) {
        return new SpongeOption(
            id,
            selectId,
            created,
            name,
            display,
            Sponge.getGame().getRegistry().getType(TextColor.class, name.toUpperCase())
                .orElse(null)
        );
    }
}
