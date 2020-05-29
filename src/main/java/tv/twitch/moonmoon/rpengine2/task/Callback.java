package tv.twitch.moonmoon.rpengine2.task;

import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.function.Consumer;

public interface Callback<T> extends Consumer<Result<T>> {
}
