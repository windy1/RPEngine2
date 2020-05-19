package tv.twitch.moonmoon.rpengine2.model;

import java.time.Instant;

/**
 * A model stored in the database
 */
public interface Model {

    /**
     * Returns the unique ID for this model
     *
     * @return Unique ID
     */
    int getId();

    /**
     * Returns the {@link Instant} this model was created
     *
     * @return Instant created
     */
    Instant getCreated();
}
