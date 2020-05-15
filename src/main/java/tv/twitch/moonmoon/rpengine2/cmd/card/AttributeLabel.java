package tv.twitch.moonmoon.rpengine2.cmd.card;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.model.RpPlayerAttribute;

import java.util.Objects;

public class AttributeLabel {

    private final String name;
    private final String value;

    public AttributeLabel(String name, String value) {
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
    }

    public static AttributeLabel from(RpPlayerAttribute attribute) {
        return new AttributeLabel(attribute.getName(), attribute.getStringValue());
    }

    @Override
    public String toString() {
        return ChatColor.BLUE + "# " + ChatColor.GREEN + name + ": " + ChatColor.WHITE + value;
    }
}
