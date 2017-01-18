package org.unicef.rapidreg.utils;

public class TextUtils {
    public static boolean isEmpty(String url) {
        return url == null || "".equals(url.trim());
    }

    public static String getLastSevenNumbers(String uniqueId) {
        if (uniqueId == null) {
            return null;
        }
        int length = uniqueId.length();
        return length > 7 ? uniqueId.substring(length - 7) : uniqueId;
    }
}
