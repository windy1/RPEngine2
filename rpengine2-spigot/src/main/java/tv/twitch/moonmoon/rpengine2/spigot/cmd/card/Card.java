package tv.twitch.moonmoon.rpengine2.spigot.cmd.card;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.spigot.model.player.SpigotRpPlayer;

import java.util.Objects;

public class Card {

    private static final String HEADER =
        ChatColor.BLUE + "========== " + ChatColor.GREEN + "%s " + ChatColor.BLUE + "==========";

    private static final String SUB_HEADER =
        "" + ChatColor.GRAY + ChatColor.ITALIC + "Click a line to edit";

    private final RpPlayerRepo playerRepo;
    private final AttributeRepo attributeRepo;
    private final SelectRepo selectRepo;
    private final RpPlayer player;
    private final boolean readOnly;

    public Card(
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        RpPlayer player,
        boolean readOnly
    ) {
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.player = Objects.requireNonNull(player);
        this.readOnly = readOnly;
    }

    public void sendTo(CommandSender sender) {
        sender.sendMessage(String.format(HEADER, playerRepo.getIdentity(player)));

        if (!readOnly) {
            sender.sendMessage(SUB_HEADER);
        }

        player.getAttributes().stream()
            .map(a -> {
                //noinspection OptionalGetWithoutIsPresent
                Attribute attribute = attributeRepo.getAttribute(a.getName()).get();
                return AttributeLabel.from(
                    a,
                    attribute.getDisplay(),
                    attribute.getFormatString().orElse(null),
                    selectRepo,
                    readOnly
                );
            })
            .forEach(a -> sender.spigot().sendMessage(a.toTextComponent()));
    }
}
