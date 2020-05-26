package tv.twitch.moonmoon.rpengine2.spigot.model.select;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.model.select.CoreOption;

import java.time.Instant;
import java.util.Optional;

public class SpigotOption extends CoreOption {

    private final ChatColor color;

    public SpigotOption(
        int id,
        int selectId,
        Instant created,
        String name,
        String display,
        ChatColor color
    ) {
        super(id, selectId, created, name, display);
        this.color = color;
    }

    /**
     * Returns this options color
     *
     * @return Option color
     */
    public Optional<ChatColor> getColor() {
        return Optional.ofNullable(color);
    }

    @Override
    public String toString() {
        return "SpigotOption{" +
            "color=" + color +
            ", id=" + id +
            ", selectId=" + selectId +
            ", created=" + created +
            ", name='" + name + '\'' +
            ", display='" + display + '\'' +
            '}';
    }
}
