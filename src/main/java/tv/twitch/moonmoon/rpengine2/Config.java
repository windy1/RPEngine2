package tv.twitch.moonmoon.rpengine2;

public interface Config {

    boolean getBoolean(String path, boolean def);

    default boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    int getInt(String path, int def);

    default int getInt(String path) {
        return getInt(path, 0);
    }

    String getString(String path, String def);

    default String getString(String path) {
        return getString(path, "");
    }

    double getDouble(String path, double def);

    default double getDouble(String path) {
        return getDouble(path, 0);
    }
}
