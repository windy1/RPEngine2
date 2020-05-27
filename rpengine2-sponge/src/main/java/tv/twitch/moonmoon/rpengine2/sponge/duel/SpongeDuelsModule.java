package tv.twitch.moonmoon.rpengine2.sponge.duel;

import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigDbo;
import tv.twitch.moonmoon.rpengine2.sponge.duel.data.SpongeDuelConfigDbo;

public class SpongeDuelsModule extends DuelsModule {

    @Override
    protected void bindDuels(AnnotatedBindingBuilder<Duels> b) {
        b.to(SpongeDuels.class);
    }

    @Override
    protected void bindConfigDbo(AnnotatedBindingBuilder<DuelConfigDbo> b) {
        b.to(SpongeDuelConfigDbo.class);
    }

    @Override
    protected void bindInvites(AnnotatedBindingBuilder<DuelInvites> b) {
        b.to(SpongeDuelInvites.class);
    }
}
