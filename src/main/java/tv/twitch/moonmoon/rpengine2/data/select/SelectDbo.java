package tv.twitch.moonmoon.rpengine2.data.select;

import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.task.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Set;

public interface SelectDbo {
    void insertOptionAsync(
        int selectId,
        String name,
        String display,
        String color,
        Callback<Void> callback
    );

    void insertSelectAsync(String name, Callback<Long> callback);

    Result<Select> selectSelect(int selectId);

    Result<Long> insertSelect(String name);

    Result<Set<Select>> selectSelects();

    Result<Void> insertOption(int selectId, String name, String display, String color);

    void deleteSelectAsync(int selectId, Callback<Void> callback);

    void deleteOptionAsync(int optionId, Callback<Void> callback);

    Result<Void> deleteOption(int optionId);

    Result<Void> deleteSelect(int selectId);

    Result<Set<Option>> selectOptions(int selectId);
}
