package tv.twitch.moonmoon.rpengine2;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.multibindings.OptionalBinder;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatModule;
import tv.twitch.moonmoon.rpengine2.chat.data.ChatConfigRepo;
import tv.twitch.moonmoon.rpengine2.chat.data.channel.ChatChannelConfigRepo;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLogModule;
import tv.twitch.moonmoon.rpengine2.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeDbo;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.attribute.CoreAttributeDbo;
import tv.twitch.moonmoon.rpengine2.data.attribute.CoreAttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.CoreRpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.CoreSelectDbo;
import tv.twitch.moonmoon.rpengine2.data.select.CoreSelectRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectDbo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerFactory;
import tv.twitch.moonmoon.rpengine2.model.select.OptionFactory;
import tv.twitch.moonmoon.rpengine2.util.AsyncExecutor;
import tv.twitch.moonmoon.rpengine2.util.Messenger;

public abstract class PluginModule extends AbstractModule {

    @Override
    protected void configure() {
        bindEngine(bind(Engine.class));
        bindPlayerRepo(bind(RpPlayerRepo.class));
        bindPlayerDbo(bind(RpPlayerDbo.class));
        bindPlayerFactory(bind(RpPlayerFactory.class));
        bindAttributeRepo(bind(AttributeRepo.class));
        bindAttributeDbo(bind(AttributeDbo.class));
        bindSelectRepo(bind(SelectRepo.class));
        bindSelectDbo(bind(SelectDbo.class));
        bindOptionFactory(bind(OptionFactory.class));
        bindAsyncExecutor(bind(AsyncExecutor.class));
        bindMessenger(bind(Messenger.class));

        OptionalBinder.newOptionalBinder(binder(), Chat.class);
        OptionalBinder.newOptionalBinder(binder(), ChatConfigRepo.class);
        OptionalBinder.newOptionalBinder(binder(), ChatChannelConfigRepo.class);

        OptionalBinder.newOptionalBinder(binder(), Duels.class);
        OptionalBinder.newOptionalBinder(binder(), DuelConfigRepo.class);

        OptionalBinder.newOptionalBinder(binder(), CombatLog.class);

        if (isChatEnabled()) {
            install(createChatModule());
        }

        if (isDuelsEnabled()) {
            install(createDuelsModule());
        }

        if (isCombatLogEnabled()) {
            install(createCombatLogModule());
        }
    }

    protected void bindEngine(AnnotatedBindingBuilder<Engine> b) {
        b.to(DefaultEngine.class);
    }

    protected abstract void bindPlayerRepo(AnnotatedBindingBuilder<RpPlayerRepo> b);

    protected void bindPlayerDbo(AnnotatedBindingBuilder<RpPlayerDbo> b) {
        b.to(CoreRpPlayerDbo.class);
    }

    protected abstract void bindPlayerFactory(AnnotatedBindingBuilder<RpPlayerFactory> b);

    protected void bindAttributeRepo(AnnotatedBindingBuilder<AttributeRepo> b) {
        b.to(CoreAttributeRepo.class);
    }

    protected void bindAttributeDbo(AnnotatedBindingBuilder<AttributeDbo> b) {
        b.to(CoreAttributeDbo.class);
    }

    protected void bindSelectRepo(AnnotatedBindingBuilder<SelectRepo> b) {
        b.to(CoreSelectRepo.class);
    }

    protected void bindSelectDbo(AnnotatedBindingBuilder<SelectDbo> b) {
        b.to(CoreSelectDbo.class);
    }

    protected abstract void bindOptionFactory(AnnotatedBindingBuilder<OptionFactory> b);

    protected abstract void bindAsyncExecutor(AnnotatedBindingBuilder<AsyncExecutor> b);

    protected abstract void bindMessenger(AnnotatedBindingBuilder<Messenger> b);

    protected abstract ChatModule createChatModule();

    protected abstract DuelsModule createDuelsModule();

    protected abstract CombatLogModule createCombatLogModule();

    protected abstract boolean isChatEnabled();

    protected abstract boolean isDuelsEnabled();

    protected abstract boolean isCombatLogEnabled();
}
