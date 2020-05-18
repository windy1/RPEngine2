package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.duel.cmd.DuelCommands;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;

public class DuelsImpl implements Duels {

    private final DuelCommands commands;
    private final DuelConfigRepo configRepo;

    @Inject
    public DuelsImpl(DuelCommands commands, DuelConfigRepo configRepo) {
        this.commands = Objects.requireNonNull(commands);
        this.configRepo = Objects.requireNonNull(configRepo);
    }

    @Override
    public Result<Void> init() {
        commands.register();

        return configRepo.load().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> Result.ok(null));
    }
}
