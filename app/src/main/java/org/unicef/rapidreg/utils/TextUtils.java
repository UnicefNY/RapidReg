package org.unicef.rapidreg.utils;

public class TextUtils {
    public static boolean isEmpty(String url) {
        return url == null || "".equals(url.trim());
    }
}
