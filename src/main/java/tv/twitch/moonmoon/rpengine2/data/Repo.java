package tv.twitch.moonmoon.rpengine2.data;

import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.Optional;
import java.util.function.Supplier;

public interface Repo {

    default <T> Result<T> handleResult(Supplier<Result<T>> f) {
        Result<T> r = f.get();
        Optional<String> err = r.getError();
        if (err.isPresent()) {
            onWarning(err.get());
            return Result.error(StringUtils.GENERIC_ERROR);
        } else {
            return r;
        }
    }

    void onWarning(String message);

    Result<Void> load();
}
