package tv.twitch.moonmoon.rpengine2.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

public class ChatMessage {

    private final String prefix;
    private final String displayName;
    private final String message;
    private final boolean actionMenu;
    private final String playerName;

    public ChatMessage(
        String prefix,
        String displayName,
        String message,
        boolean actionMenu,
        String playerName
    ) {
        this.prefix = Objects.requireNonNull(prefix);
        this.displayName = Objects.requireNonNull(displayName);
        this.message = Objects.requireNonNull(message);
        this.actionMenu = actionMenu;
        this.playerName = Objects.requireNonNull(playerName);
    }

    public TextComponent toTextComponent() {
        TextComponent c = new TextComponent(prefix);
        TextComponent n = new TextComponent(displayName + ": ");
        TextComponent m = new TextComponent(message);

        m.setColor(ChatColor.WHITE);

        if (actionMenu) {
            n.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/playeractionmenu " + playerName)
            );
        }

        c.addExtra(n);
        c.addExtra(m);

        return c;
    }
}
