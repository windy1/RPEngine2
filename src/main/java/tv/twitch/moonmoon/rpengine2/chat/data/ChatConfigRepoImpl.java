package tv.twitch.moonmoon.rpengine2.chat.data;

import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class ChatConfigRepoImpl implements ChatConfigRepo {

    private final ChatConfigDbo configDbo;
    private Map<Integer, ChatConfig> configs;
    private final Logger log;

    @Inject
    public ChatConfigRepoImpl(ChatConfigDbo configDbo, @PluginLogger Logger log) {
        this.configDbo = Objects.requireNonNull(configDbo);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public Result<ChatConfig> getConfig(RpPlayer player) {
        Objects.requireNonNull(player);
        return Optional.ofNullable(configs.get(player.getId()))
            .map(Result::ok)
            .orElseGet(() -> handleResult(() -> createConfig(player.getId())));
    }

    @Override
    public void setChannelAsync(
        RpPlayer player,
        ChatChannel channel,
        Callback<Void> callback
    ) {
        int playerId = player.getId();
        configDbo.updateChannelAsync(playerId, channel.getName(), r -> {
            callback.accept(reloadConfig(playerId));
        });
    }

    @Override
    public Result<Void> load() {
        Result<Set<ChatConfig>> r = configDbo.selectConfigs();

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        configs = Collections.synchronizedMap(r.get().stream()
            .collect(Collectors.toMap(ChatConfig::getPlayerId, Function.identity()))
        );

        return Result.ok(null);
    }

    private Result<Void> reloadConfig(int playerId) {
        Result<ChatConfig> updatedConfig = configDbo.selectConfig(playerId);

        Optional<String> err = handleResult(() -> updatedConfig).getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        ChatConfig config = updatedConfig.get();
        configs.put(playerId, config);

        return Result.ok(null);
    }

    private Result<ChatConfig> createConfig(int playerId) {
        Result<Long> r = configDbo.createConfig(playerId);

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        long configId = r.get();
        if (configId == 0) {
            return Result.ok(configs.get(playerId));
        }

        Result<ChatConfig> newConfig = configDbo.selectConfig(playerId);

        err = newConfig.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            ChatConfig c = newConfig.get();
            configs.put(playerId, c);
            return Result.ok(c);
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
