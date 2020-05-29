package tv.twitch.moonmoon.rpengine2.duel;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigDbo;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.data.impl.DefaultDuelConfigDbo;
import tv.twitch.moonmoon.rpengine2.duel.data.impl.DefaultDuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.duel.impl.DefaultDuels;

public abstract class DuelsModule extends AbstractModule {

    @Override
    protected void configure() {
        bindDuelerFactory(bind(DuelerFactory.class));
        bindConfigRepo(bind(DuelConfigRepo.class));
        bindDuels(bind(Duels.class));
        bindConfigDbo(bind(DuelConfigDbo.class));
        bindInvites(bind(DuelInvites.class));
        bindMessenger(bind(DuelMessenger.class));
    }

    protected void bindConfigRepo(AnnotatedBindingBuilder<DuelConfigRepo> b) {
        b.to(DefaultDuelConfigRepo.class);
    }

    protected abstract void bindDuelerFactory(AnnotatedBindingBuilder<DuelerFactory> b);

    protected void bindDuels(AnnotatedBindingBuilder<Duels> b) {
        b.to(DefaultDuels.class);
    }

    protected void bindConfigDbo(AnnotatedBindingBuilder<DuelConfigDbo> b) {
        b.to(DefaultDuelConfigDbo.class);
    }

    protected abstract void bindInvites(AnnotatedBindingBuilder<DuelInvites> b);

    protected abstract void bindMessenger(AnnotatedBindingBuilder<DuelMessenger> b);
}
