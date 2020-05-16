package tv.twitch.moonmoon.rpengine2.util;

import java.util.function.Consumer;

public interface Callback<T> extends Consumer<Result<T>> {
}
