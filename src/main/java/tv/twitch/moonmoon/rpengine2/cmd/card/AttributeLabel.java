package tv.twitch.moonmoon.rpengine2.cmd.card;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.select.Option;

import java.util.Objects;
import java.util.Optional;

public class AttributeLabel {

    private final String name;
    private final String display;
    private final Object value;
    private final String formatString;
    private final AttributeType type;
    private final SelectRepo selectRepo;
    private final boolean readOnly;

    public AttributeLabel(
        String name,
        String display,
        Object value,
        String formatString, AttributeType type,
        SelectRepo selectRepo,
        boolean readOnly
    ) {
        this.name = Objects.requireNonNull(name);
        this.display = Objects.requireNonNull(display);
        this.value = value;
        this.formatString = formatString;
        this.type = Objects.requireNonNull(type);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.readOnly = readOnly;
    }

    public static AttributeLabel from(
        RpPlayerAttribute attribute,
        String display,
        String formatString,
        SelectRepo selectRepo,
        boolean readOnly
    ) {
        return new AttributeLabel(
            attribute.getName(),
            display,
            attribute.getValue().orElse(null),
            formatString,
            attribute.getType(),
            selectRepo,
            readOnly
        );
    }

    private String getStringValue() {
        if (type == AttributeType.Select) {
            return getOption().map(Option::getDisplay).orElse("???");
        }

        if (value != null && formatString != null) {
            return String.format(formatString, value);
        }

        return value != null ? value.toString() : "???";
    }

    private Optional<Option> getOption() {
        return selectRepo.getSelect(name)
            .flatMap(s -> s.getOption((Integer) value));
    }

    private ClickEvent makeClickEvent() {
        if (type == AttributeType.Select) {
            String command = "/cardselect " + name;
            return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        }

        String command = String.format("/cardset %s %s", display, getStringValue());
        return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
    }

    public TextComponent toTextComponent() {
        TextComponent base = new TextComponent("# ");
        TextComponent nameTag = new TextComponent(display + ": ");
        TextComponent valueTag = new TextComponent(getStringValue());

        base.setColor(ChatColor.BLUE);
        nameTag.setColor(ChatColor.GREEN);

        ChatColor valueColor;

        if (type == AttributeType.Select) {
            valueColor = getOption().flatMap(Option::getColor)
                .map(c -> ChatColor.valueOf(c.name()))
                .orElse(ChatColor.WHITE);
        } else {
            valueColor = ChatColor.WHITE;
        }

        valueTag.setColor(valueColor);

        if (!readOnly) {
            base.setClickEvent(makeClickEvent());
        }

        base.addExtra(nameTag);
        base.addExtra(valueTag);

        return base;
    }
}
