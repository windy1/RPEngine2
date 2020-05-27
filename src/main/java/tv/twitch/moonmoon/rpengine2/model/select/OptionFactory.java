package tv.twitch.moonmoon.rpengine2.model.select;

import java.time.Instant;

public interface OptionFactory {

    Option newInstance(
        int id,
        int selectId,
        Instant created,
        String name,
        String display,
        String color
    );
}
