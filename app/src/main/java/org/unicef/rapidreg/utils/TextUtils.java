package org.unicef.rapidreg.utils;

import android.support.annotation.NonNull;

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

    @NonNull
    public static String lintUrl(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.endsWith("login")) {
            url = url.substring(0, url.indexOf("login") - 1);
        }
        return url;
    }
}
