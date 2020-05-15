package tv.twitch.moonmoon.rpengine2.cmd.card;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

    public TextComponent toTextComponent() {
        TextComponent base = new TextComponent("# ");
        TextComponent nameTag = new TextComponent(name + ": ");
        TextComponent valueTag = new TextComponent(value);

        base.setColor(net.md_5.bungee.api.ChatColor.BLUE);
        nameTag.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        valueTag.setColor(net.md_5.bungee.api.ChatColor.WHITE);

        String command = String.format("/cardset %s %s", name, value);
        base.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));

        base.addExtra(nameTag);
        base.addExtra(valueTag);

        return base;
    }
}
