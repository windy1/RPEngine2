package tv.twitch.moonmoon.rpengine2.data;

import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

public interface Repo {

    Logger getLogger();

    default <T> Result<T> handleResult(Supplier<Result<T>> f) {
        Result<T> r = f.get();
        Optional<String> err = r.getError();
        if (err.isPresent()) {
            getLogger().warning(err.get());
            return Result.error(StringUtils.GENERIC_ERROR);
        } else {
            return r;
        }
    }

    Result<Void> load();
}
