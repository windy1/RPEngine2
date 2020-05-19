package tv.twitch.moonmoon.rpengine2.duel.data;

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
public class DuelConfigRepoImpl implements DuelConfigRepo {

    private final DuelConfigDbo configDbo;
    private final Logger log;
    private Map<Integer, DuelConfig> configs;

    @Inject
    public DuelConfigRepoImpl(DuelConfigDbo configDbo, @PluginLogger Logger log) {
        this.configDbo = Objects.requireNonNull(configDbo);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public Optional<DuelConfig> getConfig(RpPlayer player) {
        Objects.requireNonNull(player);
        return Optional.ofNullable(configs.get(player.getId()));
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

    @Override
    public Logger getLogger() {
        return log;
    }
}
