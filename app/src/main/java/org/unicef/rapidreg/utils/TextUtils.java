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
        if (!url.endsWith("/")) {
            return String.format("%s/", url);
        }
        return url;
    }
}
