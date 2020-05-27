package tv.twitch.moonmoon.rpengine2.sponge.model.select;

import org.spongepowered.api.text.format.TextColor;
import tv.twitch.moonmoon.rpengine2.model.select.CoreOption;

import java.time.Instant;
import java.util.Optional;

public class SpongeOption extends CoreOption {

    private final TextColor color;

    public SpongeOption(
        int id,
        int selectId,
        Instant created,
        String name,
        String display,
        TextColor color
    ) {
        super(id, selectId, created, name, display);
        this.color = color;
    }

    public Optional<TextColor> getColor() {
        return Optional.ofNullable(color);
    }

    @Override
    public String toString() {
        return "SpongeOption{" +
            "color=" + color +
            ", id=" + id +
            ", selectId=" + selectId +
            ", created=" + created +
            ", name='" + name + '\'' +
            ", display='" + display + '\'' +
            '}';
    }
}
