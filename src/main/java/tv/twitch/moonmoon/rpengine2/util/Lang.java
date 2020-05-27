package tv.twitch.moonmoon.rpengine2.util;

import java.io.IOException;
import java.util.Properties;

public class Lang {

    private static final Properties langFile = new Properties();

    static {
        try {
            langFile.load(Lang.class.getResourceAsStream("lang.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String id, Object... args) {
        String value = getString(id);
        if (value.equals("")) {
            return value;
        }
        return String.format(value, args);
    }

    public static String getString(String id) {
        return langFile.getProperty(id, "");
    }
}
