package tv.twitch.moonmoon.rpengine2.spigot.data.select;

import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.OptionFactory;
import tv.twitch.moonmoon.rpengine2.spigot.model.select.SpigotOption;
import tv.twitch.moonmoon.rpengine2.spigot.util.SpigotUtils;

import javax.inject.Inject;
import java.time.Instant;

public class SpigotOptionFactory implements OptionFactory {

    @Inject
    public SpigotOptionFactory() {
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
        return new SpigotOption(
            id,
            selectId,
            created,
            name,
            display,
            SpigotUtils.getChatColor(color).orElse(null)
        );
    }
}
