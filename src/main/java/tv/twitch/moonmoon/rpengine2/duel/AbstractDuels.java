package tv.twitch.moonmoon.rpengine2.duel;

import tv.twitch.moonmoon.rpengine2.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public abstract class AbstractDuels implements Duels {

    protected final DuelConfigRepo configRepo;
    protected final RpPlayerRepo playerRepo;
    protected final DuelInvites invites;
    protected final Set<Duel> activeDuels = Collections.synchronizedSet(new HashSet<>());

    protected AbstractDuels(
        DuelConfigRepo configRepo,
        RpPlayerRepo playerRepo,
        DuelInvites invites
    ) {
        this.configRepo = Objects.requireNonNull(configRepo);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.invites = Objects.requireNonNull(invites);
    }

    protected abstract void startCountdown(Set<UUID> playerIds, Runnable onComplete);

    protected abstract Dueler createDueler(RpPlayer p);

    protected abstract void onDuelEnd(Dueler winner, Dueler loser);

    protected abstract void onDuelTimeout(Duel duel);

    @Override
    public void startDuel(RpPlayer p1, RpPlayer p2) {
        Set<UUID> playerIds = new HashSet<>(Arrays.asList(p1.getUUID(), p2.getUUID()));
        Duel duel = new Duel(createDueler(p1), createDueler(p2));
        activeDuels.add(duel);
        startCountdown(playerIds, duel::start);
    }

    @Override
    public void endDuel(Duel duel, Dueler winner, Dueler loser) {
        Objects.requireNonNull(duel);
        duel.getPlayer1().resetPlayer();
        duel.getPlayer2().resetPlayer();
        activeDuels.remove(duel);
        onDuelEnd(winner, loser);
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
                    onDuelTimeout(duel);

                    duel.getPlayer1().resetPlayer();
                    duel.getPlayer2().resetPlayer();
                    it.remove();
                }
            }
        }
    }
}
