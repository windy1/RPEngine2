package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.duel.cmd.DuelCommands;

import javax.inject.Inject;
import java.util.Objects;

public class DuelsImpl implements Duels {

    private final DuelCommands commands;

    @Inject
    public DuelsImpl(DuelCommands commands) {
        this.commands = Objects.requireNonNull(commands);
    }

    @Override
    public void init() {
        commands.register();
    }
}
