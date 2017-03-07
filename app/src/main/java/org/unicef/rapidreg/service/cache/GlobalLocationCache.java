package org.unicef.rapidreg.service.cache;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalLocationCache {
    private static Set<String> locationKeys = new HashSet<>();
    private static List<String> simpleLocations = new ArrayList<>();

    private GlobalLocationCache() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    static {
        Collections.addAll(locationKeys, "location_current", "relation_location_last", "relation_location_current",
                "location_separation", "location_last", "location");
        simpleLocations = new ArrayList<>();
    }

    public static void initSimpleLocations(String... locations) {
        if (simpleLocations.isEmpty()) {
            for (String location : locations) {
                simpleLocations.add(TextUtils.truncateByDoubleColons(location, PrimeroAppConfiguration
                        .getCurrentSystemSettings()
                        .getDistrictLevel()));
            }
        }
    }

    public static List<String> getSimpleLocations() {
        return simpleLocations;
    }

    public static void clearSimpleLocationCache() {
        simpleLocations.clear();
    }

    public static int index(String location) {
        return simpleLocations.indexOf(location);
    }

    public static boolean containsKey(String key) {
        return locationKeys.contains(key);
    }
}
