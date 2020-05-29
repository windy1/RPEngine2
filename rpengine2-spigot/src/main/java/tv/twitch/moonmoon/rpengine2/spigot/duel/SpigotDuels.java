package tv.twitch.moonmoon.rpengine2.spigot.duel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import tv.twitch.moonmoon.rpengine2.Config;
import tv.twitch.moonmoon.rpengine2.countdown.CountdownFactory;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.DuelMessenger;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.duel.impl.DefaultDuels;
import tv.twitch.moonmoon.rpengine2.spigot.duel.cmd.DuelCommands;
import tv.twitch.moonmoon.rpengine2.task.TaskFactory;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotDuels extends DefaultDuels {

    private final Plugin plugin;
    private final DuelCommands commands;
    private final DuelListener listener;

    @Inject
    public SpigotDuels(
        Plugin plugin,
        DuelCommands commands,
        DuelListener listener,
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo,
        DuelInvites invites,
        DuelerFactory duelerFactory,
        CountdownFactory countdownFactory,
        DuelMessenger duelMessenger,
        Messenger messenger,
        Config config,
        TaskFactory taskFactory
    ) {
        super(
            configRepo, playerRepo, invites, duelerFactory, countdownFactory, duelMessenger,
            messenger, config, taskFactory
        );

        this.plugin = Objects.requireNonNull(plugin);
        this.commands = Objects.requireNonNull(commands);
        this.listener = Objects.requireNonNull(listener);
    }

    @Override
    public Result<Void> onStarted() {
        commands.register();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        return Result.ok(null);
    }
}
