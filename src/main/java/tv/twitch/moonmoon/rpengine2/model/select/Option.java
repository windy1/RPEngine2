package tv.twitch.moonmoon.rpengine2.model.select;

import tv.twitch.moonmoon.rpengine2.model.Model;

public interface Option extends Model {
    /**
     * Returns this option's {@link Select} parent ID
     *
     * @return Parent ID
     */
    int getSelectId();

    /**
     * Returns this options name
     *
     * @return Option name
     */
    String getName();

    /**
     * Returns this options display name
     *
     * @return Option display name
     */
    String getDisplay();
}
