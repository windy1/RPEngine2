package tv.twitch.moonmoon.rpengine2.duel.impl;

import tv.twitch.moonmoon.rpengine2.countdown.CountdownFactory;
import tv.twitch.moonmoon.rpengine2.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duel;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.DuelMessenger;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.dueler.Dueler;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Lang;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class DefaultDuels implements Duels {

    protected final DuelConfigRepo configRepo;
    protected final RpPlayerRepo playerRepo;
    protected final DuelInvites invites;
    protected final DuelerFactory duelerFactory;
    protected final CountdownFactory countdownFactory;
    protected final DuelMessenger duelMessenger;
    protected final Messenger messenger;
    protected final Set<Duel> activeDuels = Collections.synchronizedSet(new HashSet<>());

    protected DefaultDuels(
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo,
        DuelInvites invites,
        DuelerFactory duelerFactory,
        CountdownFactory countdownFactory,
        DuelMessenger duelMessenger,
        Messenger messenger
    ) {
        this.configRepo = Objects.requireNonNull(configRepo);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.invites = Objects.requireNonNull(invites);
        this.duelerFactory = Objects.requireNonNull(duelerFactory);
        this.countdownFactory = Objects.requireNonNull(countdownFactory);
        this.duelMessenger = Objects.requireNonNull(duelMessenger);
        this.messenger = Objects.requireNonNull(messenger);
    }

    public Result<Void> onStarted() {
        return Result.ok(null);
    }

    @Override
    public void startDuel(RpPlayer p1, RpPlayer p2) {
        Set<UUID> playerIds = new HashSet<>(Arrays.asList(p1.getUUID(), p2.getUUID()));
        Duel duel = new DefaultDuel(duelerFactory.newInstance(p1), duelerFactory.newInstance(p2));
        activeDuels.add(duel);
        countdownFactory.newInstance(playerIds, 3).start(duel::start);
    }

    @Override
    public void endDuel(Duel duel, Dueler winner, Dueler loser) {
        Objects.requireNonNull(duel);
        duel.getPlayer1().resetPlayer();
        duel.getPlayer2().resetPlayer();
        activeDuels.remove(duel);
        duelMessenger.broadcastDuelEnd(winner, loser);
    }

    @Override
    public Optional<Duel> getActiveDuel(UUID playerId) {
        Objects.requireNonNull(playerId);

        for (Duel duel : activeDuels) {
            if (duel.getPlayer1().getPlayer().getUUID().equals(playerId)
                || duel.getPlayer2().getPlayer().getUUID().equals(playerId)) {
                return Optional.of(duel);
            }
        }

        return Optional.empty();
    }

    @Override
    public void forfeitDuel(RpPlayer player) {
        Objects.requireNonNull(player);
        Duel duel = getActiveDuel(player.getUUID()).orElse(null);

        if (duel == null) {
            messenger.sendError(player, Lang.getString("duels.noActiveDuel"));
            return;
        }

        duel.getPlayer1().resetPlayer();
        duel.getPlayer2().resetPlayer();
        activeDuels.remove(duel);

        RpPlayer winner;
        if (duel.getPlayer1().getPlayer().equals(player)) {
            winner = duel.getPlayer2().getPlayer();
        } else {
            winner = duel.getPlayer1().getPlayer();
        }

        duelMessenger.broadcastDuelForfeit(winner, player);
    }

    @Override
    public void handlePlayerJoined(RpPlayer player) {
        configRepo.getConfig(player).getError().ifPresent(messenger::warn);
    }

    @Override
    public Result<Void> init() {
        invites.startWatching();

        Optional<String> err = configRepo.load().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> Result.ok(null)).getError();

        return err
            .<Result<Void>>map(Result::error)
            .orElseGet(this::onStarted);
    }

    @Override
    public DuelConfigRepo getConfigRepo() {
        return configRepo;
    }

    @Override
    public DuelInvites getInvites() {
        return invites;
    }

    @Override
    public Set<Duel> getActiveDuels() {
        return activeDuels;
    }

    protected void cleanDuels(int maxSecs) {
        synchronized (activeDuels) {
            Iterator<Duel> it = activeDuels.iterator();

            while (it.hasNext()) {
                Duel duel = it.next();
                Optional<Instant> startTime = duel.getStartTime();

                if (!startTime.isPresent()) {
                    continue;
                }

                Instant now = Instant.now();
                long elapsedSecs = Duration.between(startTime.get(), now).toMillis() / 1000;

                if (elapsedSecs > maxSecs) {
                    duelMessenger.broadcastDuelTimeout(duel);

                    duel.getPlayer1().resetPlayer();
                    duel.getPlayer2().resetPlayer();
                    it.remove();
                }
            }
        }
    }
}
