package tv.twitch.moonmoon.rpengine2.model.select;

import tv.twitch.moonmoon.rpengine2.model.Model;
import tv.twitch.moonmoon.rpengine2.model.select.impl.DefaultSelect;

public interface Option extends Model {
    /**
     * Returns this option's {@link DefaultSelect} parent ID
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
