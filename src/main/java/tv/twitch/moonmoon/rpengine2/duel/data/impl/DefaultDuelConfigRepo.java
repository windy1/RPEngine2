package tv.twitch.moonmoon.rpengine2.duel.data.impl;

import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigDbo;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class DefaultDuelConfigRepo implements DuelConfigRepo {

    private final DuelConfigDbo configDbo;
    private final Logger log;
    private Map<Integer, DuelConfig> configs;

    @Inject
    public DefaultDuelConfigRepo(DuelConfigDbo configDbo, @PluginLogger Logger log) {
        this.configDbo = Objects.requireNonNull(configDbo);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public Result<DuelConfig> getConfig(RpPlayer player) {
        Objects.requireNonNull(player);
        int playerId = player.getId();
        return Optional.ofNullable(configs.get(playerId))
            .map(Result::ok)
            .orElseGet(() -> handleResult(() -> createConfig(playerId)));
    }

    @Override
    public Set<DuelConfig> getConfigs() {
        return Collections.unmodifiableSet(new HashSet<>(configs.values()));
    }

    @Override
    public void setRulesReadAsync(RpPlayer player) {
        Result<DuelConfig> c = getConfig(player);

        Optional<String> err = c.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            return;
        }

        if (c.get().hasReadRules()) {
            return;
        }

        int playerId = player.getId();

        configDbo.setReadRulesAsync(playerId, r -> {
            Optional<String> update = handleResult(() -> r).getError();
            if (update.isPresent()) {
                log.warning(update.get());
                return;
            }

            reloadConfig(playerId).getError()
                .ifPresent(log::warning);
        });
    }

    private Result<Void> reloadConfig(int playerId) {
        Result<DuelConfig> updatedConfig = configDbo.selectConfig(playerId);

        Optional<String> err = handleResult(() -> updatedConfig).getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        DuelConfig config = updatedConfig.get();
        configs.put(playerId, config);

        return Result.ok(null);
    }

    @Override
    public void onWarning(String message) {
        log.warning(message);
    }

    @Override
    public Result<Void> load() {
        Result<Set<DuelConfig>> r = configDbo.selectConfigs();

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        configs = Collections.synchronizedMap(r.get().stream()
            .collect(Collectors.toMap(DuelConfig::getPlayerId, Function.identity()))
        );

        return Result.ok(null);
    }

    private Result<DuelConfig> createConfig(int playerId) {
        Result<Long> r = configDbo.insertConfig(playerId);

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        long configId = r.get();
        if (configId == 0) {
            return Result.ok(configs.get(playerId));
        }

        Result<DuelConfig> newConfig = configDbo.selectConfig(playerId);

        err = newConfig.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            DuelConfig c = newConfig.get();
            configs.put(playerId, c);
            return Result.ok(c);
        }
    }

}
