package org.unicef.rapidreg.service.cache;

import java.util.HashMap;
import java.util.Map;

public class CaseFieldValueCache {
    private static Map<String, Object> values = new HashMap<>();

    public static Map<String, Object> getValues() {
        return values;
    }

    public static Object get(String key) {
        return values.get(key);
    }
}
