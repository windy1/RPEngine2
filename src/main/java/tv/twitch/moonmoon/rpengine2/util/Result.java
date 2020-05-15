package tv.twitch.moonmoon.rpengine2.util;

import java.util.Optional;
import java.util.function.Function;

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

    public T orElse(T t) {
        return Optional.ofNullable(result).orElse(t);
    }

    public Optional<String> getError() {
        return Optional.ofNullable(error);
    }

    public <U> Result<U> mapOk(Function<T, U> f) {
        return getError()
            .<Result<U>>map(Result::error)
            .orElseGet(() -> Result.ok(f.apply(result)));
    }

    public static <T> Result<T> ok(T result) {
        return new Result<>(result, null);
    }

    public static <T> Result<T> error(String err) {
        return new Result<>(null, err);
    }
}
