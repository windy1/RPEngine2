package tv.twitch.moonmoon.rpengine2.sponge;

import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import org.slf4j.Logger;
import tv.twitch.moonmoon.rpengine2.PluginModule;
import tv.twitch.moonmoon.rpengine2.chat.ChatModule;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLogModule;
import tv.twitch.moonmoon.rpengine2.countdown.CountdownFactory;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerFactory;
import tv.twitch.moonmoon.rpengine2.model.select.OptionFactory;
import tv.twitch.moonmoon.rpengine2.sponge.chat.SpongeChatModule;
import tv.twitch.moonmoon.rpengine2.sponge.combatlog.SpongeCombatLogModule;
import tv.twitch.moonmoon.rpengine2.sponge.countdown.SpongeCountdownFactory;
import tv.twitch.moonmoon.rpengine2.sponge.data.player.SpongeRpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.sponge.data.select.SpongeOptionFactory;
import tv.twitch.moonmoon.rpengine2.sponge.duel.SpongeDuelsModule;
import tv.twitch.moonmoon.rpengine2.sponge.model.player.SpongeRpPlayerFactory;
import tv.twitch.moonmoon.rpengine2.sponge.util.SpongeAsyncExecutor;
import tv.twitch.moonmoon.rpengine2.sponge.util.SpongeMessenger;
import tv.twitch.moonmoon.rpengine2.util.AsyncExecutor;
import tv.twitch.moonmoon.rpengine2.util.DbPath;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.PluginLogger;

import java.nio.file.Path;
import java.util.Objects;

public class SpongeModule extends PluginModule {

    private final RpEngine2 plugin;
    private final Path dbPath;
    private final Config config;
    private final Logger log;

    public SpongeModule(RpEngine2 plugin, Path dbPath, Config config, Logger log) {
        this.plugin = Objects.requireNonNull(plugin);
        this.dbPath = Objects.requireNonNull(dbPath);
        this.config = Objects.requireNonNull(config);
        this.log = Objects.requireNonNull(log);
    }

    @Provides
    public RpEngine2 providePlugin() {
        return plugin;
    }

    @Provides
    public Config provideConfig() {
        return config;
    }

    @Provides
    @PluginLogger
    public Logger provideLogger() {
        return log;
    }

    @Provides
    @DbPath
    public Path provideDbPath() {
        return dbPath;
    }

    @Override
    protected void bindPlayerRepo(AnnotatedBindingBuilder<RpPlayerRepo> b) {
        b.to(SpongeRpPlayerRepo.class);
    }

    @Override
    protected void bindPlayerFactory(AnnotatedBindingBuilder<RpPlayerFactory> b) {
        b.to(SpongeRpPlayerFactory.class);
    }

    @Override
    protected void bindOptionFactory(AnnotatedBindingBuilder<OptionFactory> b) {
        b.to(SpongeOptionFactory.class);
    }

    @Override
    protected void bindAsyncExecutor(AnnotatedBindingBuilder<AsyncExecutor> b) {
        b.to(SpongeAsyncExecutor.class);
    }

    @Override
    protected void bindMessenger(AnnotatedBindingBuilder<Messenger> b) {
        b.to(SpongeMessenger.class);
    }

    @Override
    protected void bindCountdownFactory(AnnotatedBindingBuilder<CountdownFactory> b) {
        b.to(SpongeCountdownFactory.class);
    }

    @Override
    protected ChatModule createChatModule() {
        return new SpongeChatModule();
    }

    @Override
    protected DuelsModule createDuelsModule() {
        return new SpongeDuelsModule();
    }

    @Override
    protected CombatLogModule createCombatLogModule() {
        return new SpongeCombatLogModule();
    }

    @Override
    protected boolean isChatEnabled() {
        return config.getRoot().getNode("chat", "enabled").getBoolean();
    }

    @Override
    protected boolean isDuelsEnabled() {
        return config.getRoot().getNode("duels", "enabled").getBoolean();
    }

    @Override
    protected boolean isCombatLogEnabled() {
        return config.getRoot().getNode("combatlog", "enabled").getBoolean();
    }
}
