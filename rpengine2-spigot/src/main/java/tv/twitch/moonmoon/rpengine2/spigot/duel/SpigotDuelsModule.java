package tv.twitch.moonmoon.rpengine2.spigot.duel;

import com.google.inject.binder.AnnotatedBindingBuilder;
import tv.twitch.moonmoon.rpengine2.duel.DuelInvites;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.duel.DuelsModule;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigDbo;
import tv.twitch.moonmoon.rpengine2.spigot.duel.data.SpigotDuelConfigDbo;

public class SpigotDuelsModule extends DuelsModule {

    @Override
    protected void bindDuels(AnnotatedBindingBuilder<Duels> b) {
        b.to(SpigotDuels.class);
    }

    @Override
    protected void bindConfigDbo(AnnotatedBindingBuilder<DuelConfigDbo> b) {
        b.to(SpigotDuelConfigDbo.class);
    }

    @Override
    protected void bindInvites(AnnotatedBindingBuilder<DuelInvites> b) {
        b.to(SpigotDuelInvites.class);
    }
}
