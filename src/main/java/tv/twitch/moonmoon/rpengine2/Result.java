package tv.twitch.moonmoon.rpengine2;

import java.util.Optional;

public class Result<T> {

    private final T result;
    private final String error;

    private Result(T result, String error) {
        this.result = result;
        this.error = error;
    }

    public T get() {
        return Optional.ofNullable(result)
            .orElseThrow(() -> new IllegalStateException("expected result"));
    }

    public Optional<String> getError() {
        return Optional.ofNullable(error);
    }

    public static <T> Result<T> ok(T result) {
        return new Result<>(result, null);
    }

    public static <T> Result<T> error(String err) {
        return new Result<>(null, err);
    }
}
