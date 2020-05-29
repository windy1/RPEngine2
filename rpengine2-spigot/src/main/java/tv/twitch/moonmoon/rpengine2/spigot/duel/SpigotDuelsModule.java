package tv.twitch.moonmoon.rpengine2.spigot.duel;

import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.duel.DuelMessenger;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.duel.dueler.DuelerFactory;
import tv.twitch.moonmoon.rpengine2.spigot.duel.dueler.SpigotDuelerFactory;

public class SpigotDuelsModule extends DuelsModule {

    @Override
    protected void bindDuelerFactory(AnnotatedBindingBuilder<DuelerFactory> b) {
        b.to(SpigotDuelerFactory.class);
    }

    @Override
    protected void bindDuels(AnnotatedBindingBuilder<Duels> b) {
        b.to(SpigotDuels.class);
    }

    @Override
    protected void bindMessenger(AnnotatedBindingBuilder<DuelMessenger> b) {
        b.to(SpigotDuelMessenger.class);
    }
}
