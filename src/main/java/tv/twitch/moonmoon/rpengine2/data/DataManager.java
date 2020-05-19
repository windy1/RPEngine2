package tv.twitch.moonmoon.rpengine2.data;

import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;

/**
 * This class is responsible for handling the core data models in the plugin.
 */
public interface DataManager {

    /**
     * Returns the {@link RpPlayerRepo} instance responsible for managing player data
     *
     * @return Player repo
     */
    RpPlayerRepo getPlayerRepo();

    /**
     * Returns the {@link AttributeRepo} instance responsible for managing attribute data
     *
     * @return Attribute repo
     */
    AttributeRepo getAttributeRepo();

    /**
     * Returns the {@link SelectRepo} instance responsible for managing select data
     *
     * @return Select repo
     */
    SelectRepo getSelectRepo();
}
