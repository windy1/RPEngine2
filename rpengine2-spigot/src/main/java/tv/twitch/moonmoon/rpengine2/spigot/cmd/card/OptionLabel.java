package tv.twitch.moonmoon.rpengine2.spigot.cmd.card;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

public class OptionLabel {

    private final String optionDisplay;
    private final String selectName;
    private final String optionName;
    private final org.bukkit.ChatColor optionColor;
    private final boolean active;

    public OptionLabel(
        String optionDisplay,
        String selectName,
        String optionName,
        org.bukkit.ChatColor optionColor,
        boolean active
    ) {
        this.optionDisplay = Objects.requireNonNull(optionDisplay);
        this.selectName = Objects.requireNonNull(selectName);
        this.optionName = Objects.requireNonNull(optionName);
        this.optionColor = optionColor;
        this.active = active;
    }

    public TextComponent toTextComponent() {
        TextComponent base = new TextComponent();
        TextComponent act = new TextComponent(active ? "* " : "");
        TextComponent optionLabel = new TextComponent(optionDisplay);

        act.setColor(ChatColor.GREEN);
        act.setBold(true);

        if (optionColor != null) {
            optionLabel.setColor(ChatColor.valueOf(optionColor.name()));
        }

        base.addExtra(act);
        base.addExtra(optionLabel);

        String command = String.format("/cardselect %s %s", selectName, optionName);
        base.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        return base;
    }
}
