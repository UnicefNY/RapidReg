package org.unicef.rapidreg.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    public static Map convert(String mapString) {
        Map res = new HashMap<>();

        String[] pairs = mapString.substring(1, mapString.length() - 1).split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.trim().split("=");

            checkFormat(keyValue.length);
            res.put(keyValue[0], keyValue[1]);
        }
        return res;
    }

    private static void checkFormat(int len) {
        if (len < 2) {
            throw new IllegalArgumentException();
        }
    }
}
