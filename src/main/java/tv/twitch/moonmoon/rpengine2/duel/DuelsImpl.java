package tv.twitch.moonmoon.rpengine2.duel;

import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.cmd.Countdown;
import tv.twitch.moonmoon.rpengine2.duel.cmd.DuelCommands;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.*;

public class DuelsImpl implements Duels {

    private final Plugin plugin;
    private final DuelCommands commands;
    private final DuelConfigRepo configRepo;

    @Inject
    public DuelsImpl(Plugin plugin, DuelCommands commands, DuelConfigRepo configRepo) {
        this.plugin = Objects.requireNonNull(plugin);
        this.commands = Objects.requireNonNull(commands);
        this.configRepo = Objects.requireNonNull(configRepo);
    }

    @Override
    public void startDuel(RpPlayer p1, RpPlayer p2) {
        Set<UUID> playerIds = new HashSet<>(Arrays.asList(p1.getUUID(), p2.getUUID()));
        Countdown.from(plugin.getConfig(), playerIds, 3, () -> {
            System.out.println("GOGOGOGOGOGOGOG");
        });
    }

    @Override
    public Result<Void> init() {
        commands.register();

        return configRepo.load().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> Result.ok(null));
    }
}
