package tv.twitch.moonmoon.rpengine2.chat.data.channel;

import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatChannelConfig;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.logging.Logger;

@Singleton
public class ChatChannelConfigRepoImpl implements ChatChannelConfigRepo {

    private final ChatChannelConfigDbo configDbo;
    private final Logger log;
    private Map<Integer, Map<String, ChatChannelConfig>> configs;

    @Inject
    public ChatChannelConfigRepoImpl(ChatChannelConfigDbo configDbo, @PluginLogger Logger log) {
        this.configDbo = Objects.requireNonNull(configDbo);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public Result<ChatChannelConfig> getConfig(RpPlayer player, ChatChannel channel) {
        Objects.requireNonNull(channel);
        Objects.requireNonNull(player);
        int playerId = player.getId();
        return Optional.ofNullable(configs.get(playerId))
            .map(c -> c.get(channel.getName()))
            .map(Result::ok)
            .orElseGet(() -> handleResult(() -> createConfig(playerId, channel.getName())));
    }

    @Override
    public Set<Map<String, ChatChannelConfig>> getConfigs() {
        return Collections.unmodifiableSet(new HashSet<>(configs.values()));
    }

    @Override
    public void toggleMutedAsync(
        RpPlayer player,
        ChatChannel channel,
        Callback<Boolean> callback
    ) {
        Result<ChatChannelConfig> c = getConfig(player, channel);
        ChatChannelConfig config;
        boolean muted;
        int playerId = player.getId();
        String channelName = channel.getName();

        Optional<String> err = c.getError();
        if (err.isPresent()) {
            callback.accept(Result.error(err.get()));
            return;
        }
        config = c.get();

        muted = !config.isMuted();

        configDbo.setMutedAsync(playerId, channelName, muted, r -> {
            Optional<String> updateErr = handleResult(() -> r).getError();
            if (updateErr.isPresent()) {
                callback.accept(Result.error(updateErr.get()));
                return;
            }

            // reload config
            updateErr = reloadConfig(playerId, channelName).getError();
            if (updateErr.isPresent()) {
                callback.accept(Result.error(updateErr.get()));
            } else {
                callback.accept(Result.ok(muted));
            }
        });
    }

    private Result<Void> reloadConfig(int playerId, String channelName) {
        Result<ChatChannelConfig> updatedConfig = handleResult(() ->
            configDbo.selectConfig(playerId, channelName)
        );

        Optional<String> err = updatedConfig.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        ChatChannelConfig config = updatedConfig.get();

        configs.computeIfAbsent(playerId, k -> new HashMap<>())
            .put(channelName, config);

        return Result.ok(null);
    }

    @Override
    public Result<Void> load() {
        Result<Set<ChatChannelConfig>> c = configDbo.selectConfigs();

        Optional<String> err = c.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            Set<ChatChannelConfig> configs = c.get();
            Map<Integer, Map<String, ChatChannelConfig>> configMap = new HashMap<>();

            for (ChatChannelConfig config : configs) {
                configMap.computeIfAbsent(config.getPlayerId(), k -> new HashMap<>())
                    .put(config.getChannelName(), config);
            }

            this.configs = Collections.synchronizedMap(configMap);

            return Result.ok(null);
        }
    }

    private Result<ChatChannelConfig> createConfig(int playerId, String channelName) {
        Result<Long> r = configDbo.createConfig(playerId, channelName);

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        long configId = r.get();
        if (configId == 0) {
            // config already existed
            return Result.ok(configs.get(playerId).get(channelName));
        }

        Result<ChatChannelConfig> newConfig = configDbo.selectConfig(playerId, channelName);

        err = newConfig.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            ChatChannelConfig c = newConfig.get();
            configs.computeIfAbsent(playerId, k -> new HashMap<>())
                .put(channelName, c);

            return Result.ok(c);
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
