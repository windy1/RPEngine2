package tv.twitch.moonmoon.rpengine2.countdown;

/**
 * Represents a countdown that is broadcasted to nearby (or all) players
 */
public interface Countdown {

    /**
     * Starts the countdown and invokes the specified callback once completed
     *
     * @param callback Callback to invoke
     */
    void start(Runnable callback);

    /**
     * Starts the countdown
     */
    default void start() {
        start(null);
    }
}
