package tv.twitch.moonmoon.rpengine2.task;

public interface Task {

    void setInterval(Runnable r, long delayMillis, long periodMillis);

    void setIntervalAsync(Runnable r, long delayMillis, long periodMillis);

    boolean isCancelled();

    void cancel();
}
