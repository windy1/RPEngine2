package tv.twitch.moonmoon.rpengine2.duel;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.data.CoreDuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigDbo;

public abstract class DuelsModule extends AbstractModule {

    @Override
    protected void configure() {
        bindConfigRepo(bind(DuelConfigRepo.class));
        bindDuels(bind(Duels.class));
        bindConfigDbo(bind(DuelConfigDbo.class));
        bindInvites(bind(DuelInvites.class));
    }

    protected void bindConfigRepo(AnnotatedBindingBuilder<DuelConfigRepo> b) {
        b.to(CoreDuelConfigRepo.class);
    }

    protected abstract void bindDuels(AnnotatedBindingBuilder<Duels> b);

    protected abstract void bindConfigDbo(AnnotatedBindingBuilder<DuelConfigDbo> b);

    protected abstract void bindInvites(AnnotatedBindingBuilder<DuelInvites> b);
}
