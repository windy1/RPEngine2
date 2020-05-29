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
import tv.twitch.moonmoon.rpengine2.countdown.CountdownFactory;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeDbo;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.attribute.impl.DefaultAttributeDbo;
import tv.twitch.moonmoon.rpengine2.data.attribute.impl.DefaultAttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.player.impl.DefaultRpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectDbo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.data.select.impl.DefaultSelectDbo;
import tv.twitch.moonmoon.rpengine2.data.select.impl.DefaultSelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.impl.DefaultEngine;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerFactory;
import tv.twitch.moonmoon.rpengine2.model.select.OptionFactory;
import tv.twitch.moonmoon.rpengine2.task.TaskExecutor;
import tv.twitch.moonmoon.rpengine2.task.TaskFactory;
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
        bindAsyncExecutor(bind(TaskExecutor.class));
        bindMessenger(bind(Messenger.class));
        bindCountdownFactory(bind(CountdownFactory.class));
        bindTaskFactory(bind(TaskFactory.class));

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
        b.to(DefaultRpPlayerDbo.class);
    }

    protected abstract void bindPlayerFactory(AnnotatedBindingBuilder<RpPlayerFactory> b);

    protected void bindAttributeRepo(AnnotatedBindingBuilder<AttributeRepo> b) {
        b.to(DefaultAttributeRepo.class);
    }

    protected void bindAttributeDbo(AnnotatedBindingBuilder<AttributeDbo> b) {
        b.to(DefaultAttributeDbo.class);
    }

    protected void bindSelectRepo(AnnotatedBindingBuilder<SelectRepo> b) {
        b.to(DefaultSelectRepo.class);
    }

    protected void bindSelectDbo(AnnotatedBindingBuilder<SelectDbo> b) {
        b.to(DefaultSelectDbo.class);
    }

    protected abstract void bindOptionFactory(AnnotatedBindingBuilder<OptionFactory> b);

    protected abstract void bindAsyncExecutor(AnnotatedBindingBuilder<TaskExecutor> b);

    protected abstract void bindMessenger(AnnotatedBindingBuilder<Messenger> b);

    protected abstract void bindCountdownFactory(AnnotatedBindingBuilder<CountdownFactory> b);

    protected abstract void bindTaskFactory(AnnotatedBindingBuilder<TaskFactory> b);

    protected abstract ChatModule createChatModule();

    protected abstract DuelsModule createDuelsModule();

    protected abstract CombatLogModule createCombatLogModule();

    protected abstract boolean isChatEnabled();

    protected abstract boolean isDuelsEnabled();

    protected abstract boolean isCombatLogEnabled();
}
