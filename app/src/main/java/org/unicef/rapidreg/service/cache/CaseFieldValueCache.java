package org.unicef.rapidreg.service.cache;

import java.util.HashMap;
import java.util.Map;

public class CaseFieldValueCache {
    private static Map<String, String> values = new HashMap<>();

    public static Map<String, String> getValues() {
        return values;
    }

    public static void setValues(Map<String, String> valuesMap) {
        values.putAll(valuesMap);
    }

    public static void put(String key, String value) {
        values.put(key, value);
    }

    public static String get(String key) {
        return values.get(key);
    }

    public static void clear() {
        values.clear();
    }
}
