package org.unicef.rapidreg.service.cache;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubformCache {
    private static Map<String, List<Map<String, String>>> values = new HashMap<>();

    public static Map<String, List<Map<String, String>>> getValues() {
        return values;
    }

    public static void put(String key, List<Map<String, String>> fields) {
        values.put(key, fields);
    }

    public static List<Map<String, String>> get(String key) {
        return values.get(key);
    }

    public static void clear() {
        values.clear();
    }

    public static String toJson() {
        return new Gson().toJson(values);
    }
}
