package tv.twitch.moonmoon.rpengine2.sponge.duel;

import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.duel.DuelMessenger;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.sponge.duel.dueler.SpongeDuelerFactory;

public class SpongeDuelsModule extends DuelsModule {

    @Override
    protected void bindDuelerFactory(AnnotatedBindingBuilder<DuelerFactory> b) {
        b.to(SpongeDuelerFactory.class);
    }

    @Override
    protected void bindDuels(AnnotatedBindingBuilder<Duels> b) {
        b.to(SpongeDuels.class);
    }

    @Override
    protected void bindMessenger(AnnotatedBindingBuilder<DuelMessenger> b) {
        b.to(SpongeDuelMessenger.class);
    }
}
