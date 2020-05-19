package tv.twitch.moonmoon.rpengine2.duel;

import com.google.inject.AbstractModule;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepo;
import tv.twitch.moonmoon.rpengine2.duel.data.DuelConfigRepoImpl;

public class DuelModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Duels.class).to(DuelsImpl.class);
        bind(DuelConfigRepo.class).to(DuelConfigRepoImpl.class);
    }
}
