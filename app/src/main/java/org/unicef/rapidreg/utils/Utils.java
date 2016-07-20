package org.unicef.rapidreg.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static String toStringResult(List<String> result) {
        String res = "";

        if (result == null) {
            return res;
        }

        for (int i = 0; i < result.size(); i++) {
            String item = result.get(i);
            if (i == 0) {
                res += item;
            } else {
                res += "," + item;
            }
        }

        return res;
    }

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
