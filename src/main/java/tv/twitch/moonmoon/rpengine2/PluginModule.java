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
import tv.twitch.moonmoon.rpengine2.data.Defaults;
import tv.twitch.moonmoon.rpengine2.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeDbo;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.attribute.CoreAttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerDbo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectDbo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;

public abstract class PluginModule extends AbstractModule {

    @Override
    protected void configure() {
        bindDefaults(bind(Defaults.class));
        bindAttributeRepo(bind(AttributeRepo.class));
        bindAttributeDbo(bind(AttributeDbo.class));
        bindPlayerRepo(bind(RpPlayerRepo.class));
        bindPlayerDbo(bind(RpPlayerDbo.class));
        bindSelectRepo(bind(SelectRepo.class));
        bindSelectDbo(bind(SelectDbo.class));

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

    protected void bindAttributeRepo(AnnotatedBindingBuilder<AttributeRepo> b) {
        b.to(CoreAttributeRepo.class);
    }

    protected abstract void bindAttributeDbo(AnnotatedBindingBuilder<AttributeDbo> b);

    protected abstract void bindDefaults(AnnotatedBindingBuilder<Defaults> b);

    protected abstract void bindPlayerRepo(AnnotatedBindingBuilder<RpPlayerRepo> b);

    protected abstract void bindSelectRepo(AnnotatedBindingBuilder<SelectRepo> b);

    protected abstract void bindSelectDbo(AnnotatedBindingBuilder<SelectDbo> b);

    protected abstract void bindPlayerDbo(AnnotatedBindingBuilder<RpPlayerDbo> b);

    protected abstract ChatModule createChatModule();

    protected abstract DuelsModule createDuelsModule();

    protected abstract CombatLogModule createCombatLogModule();

    protected abstract boolean isChatEnabled();

    protected abstract boolean isDuelsEnabled();

    protected abstract boolean isCombatLogEnabled();
}
