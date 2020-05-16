package tv.twitch.moonmoon.rpengine2.data.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class RpPlayerRepoImpl implements RpPlayerRepo {

    private final Plugin plugin;
    private final RpPlayerDbo playerDbo;
    private final AttributeRepo attributeRepo;
    private final Logger log;
    private final Queue<OfflinePlayer> joinedPlayers = new ConcurrentLinkedQueue<>();

    private Map<String, RpPlayer> players;
    private Map<String, RpPlayer> playerNameMap;
    private BukkitTask joinedPlayersWatcher;

    @Inject
    public RpPlayerRepoImpl(Plugin plugin, RpPlayerDbo playerDbo, AttributeRepo attributeRepo) {
        this.plugin = Objects.requireNonNull(plugin);
        this.playerDbo = Objects.requireNonNull(playerDbo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        log = plugin.getLogger();
    }

    @Override
    public Set<RpPlayer> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<>(players.values()));
    }

    @Override
    public Result<RpPlayer> getPlayer(OfflinePlayer player) {
        Objects.requireNonNull(player);
        return Optional.ofNullable(players.get(player.getUniqueId().toString()))
            .map(Result::ok)
            .orElseGet(() -> handleResult(() -> createPlayer(player)));
    }

    @Override
    public Optional<RpPlayer> getPlayer(String name) {
        return Optional.ofNullable(playerNameMap.get(name));
    }

    @Override
    public void setAttributeAsync(
        RpPlayer player,
        int attributeId,
        Object value,
        Consumer<Result<Void>> callback
    ) {
        UUID playerId = player.getUUID();
        Optional<RpPlayerAttribute> existing = player.getAttribute(attributeId);

        if (existing.isPresent()) {
            playerDbo.updatePlayerAttributeAsync(player.getId(), attributeId, value, r ->
                callback.accept(handleAndReloadPlayer(playerId, r))
            );
        } else {
            playerDbo.insertPlayerAttributeAsync(player.getId(), attributeId, value, r ->
                callback.accept(handleAndReloadPlayer(playerId, r))
            );
        }
    }

    @Override
    public void removeAttributesAsync(int attributeId, Consumer<Result<Void>> callback) {
        playerDbo.deletePlayerAttributesAsync(attributeId, r ->
            callback.accept(handleAndReloadPlayers(r))
        );
    }

    @Override
    public Result<Void> load() {
        Result<Set<RpPlayer>> r = playerDbo.selectPlayers();

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            onLoad(r.get());
            startJoinedPlayersWatcher();
            return Result.ok(null);
        }
    }

    @Override
    public Result<Void> reloadPlayers() {
        Result<Set<RpPlayer>> reloadedPlayers = handleResult(playerDbo::selectPlayers);

        Optional<String> err = reloadedPlayers.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            onLoad(reloadedPlayers.get());
            return Result.ok(null);
        }
    }

    @Override
    public void handlePlayerJoined(OfflinePlayer player) {
        joinedPlayers.add(player);
    }

    @Override
    public void flushJoinedPlayers() {
        synchronized (joinedPlayers) {
            while (!joinedPlayers.isEmpty()) {
                createPlayer(joinedPlayers.poll()).getError()
                    .ifPresent(log::info);
            }
        }
    }

    private Result<RpPlayer> createPlayer(OfflinePlayer player) {
        Result<Long> newPlayerId = playerDbo.insertPlayer(player);
        Optional<String> err = newPlayerId.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        UUID playerId = player.getUniqueId();

        if (newPlayerId.get() == 0) {
            // player already existed
            return Result.ok(players.get(playerId.toString()));
        }

        Result<RpPlayer> p = reloadPlayer(playerId);

        err = p.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        RpPlayer newPlayer = p.get();

        for (Attribute attribute : attributeRepo.getAttributes()) {
            Object defaultValue = attribute.getDefaultValue().orElse(null);
            setAttributeAsync(newPlayer, attribute.getId(), defaultValue, r ->
                r.getError().ifPresent(log::warning)
            );
        }

        return Result.ok(newPlayer);
    }

    private <T> Result<Void> handleAndReloadPlayer(UUID playerId, Result<T> r) {
        Optional<String> err = handleResult(() -> r).getError();
        return err
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> handleResult(() -> reloadPlayer(playerId)).getError()
                .<Result<Void>>map(Result::error)
                .orElseGet(() -> Result.ok(null))
            );
    }

    private <T> Result<Void> handleAndReloadPlayers(Result<T> r) {
        Optional<String> err = handleResult(() -> r).getError();
        return err
            .<Result<Void>>map(Result::error)
            .orElseGet(this::reloadPlayers);

    }

    private void updatePlayer(RpPlayer player) {
        players.put(player.getUUID().toString(), player);
        playerNameMap.put(player.getUsername(), player);
    }

    private void startJoinedPlayersWatcher() {
        joinedPlayersWatcher = Bukkit.getScheduler().runTaskTimerAsynchronously(
            plugin,
            this::flushJoinedPlayers,
            0, 20
        );
    }

    private Result<RpPlayer> reloadPlayer(UUID playerId) {
        Result<RpPlayer> updatedPlayer = playerDbo.selectPlayer(playerId);

        Optional<String> err = updatedPlayer.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        RpPlayer p = updatedPlayer.get();
        updatePlayer(p);

        return Result.ok(p);
    }

    private void onLoad(Set<RpPlayer> loadedPlayers) {
        players = Collections.synchronizedMap(loadedPlayers.stream()
            .collect(Collectors.toMap(p -> p.getUUID().toString(), Function.identity())));
        playerNameMap = Collections.synchronizedMap(loadedPlayers.stream()
            .collect(Collectors.toMap(RpPlayer::getUsername, Function.identity())));
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (joinedPlayersWatcher != null && !joinedPlayersWatcher.isCancelled()) {
            joinedPlayersWatcher.cancel();
        }
    }
}
