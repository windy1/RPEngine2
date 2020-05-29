package tv.twitch.moonmoon.rpengine2.data.attribute;

import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.task.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Set;

public interface AttributeDbo {
    void insertAttributeAsync(
            String name,
            String display,
            String type,
            String defaultValue,
            Callback<Long> callback
    );

    Result<Attribute> selectAttribute(String name);

    Result<Void> deleteAttribute(int attributeId);

    Result<Long> insertAttribute(
            String name,
            String display,
            String type,
            String defaultValue
    );

    Result<Set<Attribute>> selectAttributes();

    void updateDefaultAsync(int attributeId, String defaultValue, Callback<Void> callback);

    void updateDisplayAsync(int attributeId, String display, Callback<Void> callback);

    void updateFormatAsync(int attributeId, String formatString, Callback<Void> callback);

    void setIdentityAsync(int attributeId, Callback<Void> callback);

    void clearIdentityAsync(Callback<Void> callback);

    Result<Void> setIdentity(int attributeId);

    void setMarkerAsync(int attributeId, Callback<Void> callback);

    Result<Void> setMarker(int attributeId);

    void clearMarkerAsync(Callback<Void> callback);

    void setTitleAsync(int attributeId, Callback<Void> callback);

    Result<Void> setTitle(int attributeId);

    void clearTitleAsync(Callback<Void> callback);
}
