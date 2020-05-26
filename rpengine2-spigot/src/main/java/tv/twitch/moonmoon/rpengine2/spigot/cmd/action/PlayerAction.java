package tv.twitch.moonmoon.rpengine2.spigot.cmd.action;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

public class PlayerAction {

    private final String name;
    private final ChatColor color;
    private final ClickEvent clickEvent;

    public PlayerAction(String name, ChatColor color, ClickEvent clickEvent) {
        this.name = Objects.requireNonNull(name);
        this.color = Objects.requireNonNull(color);
        this.clickEvent = Objects.requireNonNull(clickEvent);
    }

    public TextComponent toTextComponent() {
        String nameDisplay = String.format(" [%s] ", name);
        int minLength = 20;
        int padding = (minLength - nameDisplay.length()) / 2;
        StringBuilder padLeft = new StringBuilder("#");
        StringBuilder padRight = new StringBuilder();

        for (int i = 0; i < padding; i++) {
            padLeft.append(' ');
            padRight.insert(0, ' ');
        }

        TextComponent left = new TextComponent(padLeft.toString());
        TextComponent action = new TextComponent(nameDisplay);
        TextComponent right = new TextComponent(padRight.toString());

        left.setColor(ChatColor.BLUE);
        action.setColor(color);
        right.setColor(ChatColor.BLUE);

        action.setClickEvent(clickEvent);

        left.addExtra(action);
        left.addExtra(right);

        return left;
    }

    public static PlayerAction inspect(String targetName) {
        return new PlayerAction("Inspect", ChatColor.GREEN, new ClickEvent(
            ClickEvent.Action.RUN_COMMAND, "/inspect " + targetName
        ));
    }

    public static PlayerAction bird(String targetName) {
        return new PlayerAction("Bird", ChatColor.LIGHT_PURPLE, new ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND, "/bird " + targetName + " "
        ));
    }
}
