package tv.twitch.moonmoon.rpengine2.util;

import java.util.Arrays;

public class StringUtils {

    public static final String GENERIC_ERROR =
        "An unexpected error occurred. See console for details";

    public static String[] splice(String[] args, int start) {
        if (start > args.length - 1) {
            return new String[0];
        } else {
            return Arrays.copyOfRange(args, start, args.length);
        }
    }
}
