package org.unicef.rapidreg.utils;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

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

    public static String truncateByDoubleColons(String source, int level) {
        if (isEmpty(source)) {
            return source;
        }
        if (level <= 0) {
            throw new IllegalArgumentException("Level must be larger than 0");
        }
        List<String> original = Arrays.asList(source.split("::"));
        if (original.size() <= level) {
            return source;
        }
        List<String> result = original.subList(level, original.size());
        return join("::", result.toArray(new String[0]));
    }

    private static String join(CharSequence separator, CharSequence... elements) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (CharSequence element : elements) {
            builder.append(element);
            if (index++ < elements.length - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }
}
