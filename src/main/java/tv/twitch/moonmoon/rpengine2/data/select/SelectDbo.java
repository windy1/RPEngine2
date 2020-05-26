package tv.twitch.moonmoon.rpengine2.data.select;

import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Set;

public interface SelectDbo {
    void insertSelectAsync(String name, Callback<Long> callback);

    Result<Select> selectSelect(int selectId);

    Result<Long> insertSelect(String name);

    Result<Set<Select>> selectSelects();

    void deleteSelectAsync(int selectId, Callback<Void> callback);

    void deleteOptionAsync(int optionId, Callback<Void> callback);
}
