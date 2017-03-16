package org.unicef.rapidreg.service.cache;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalLocationCache {
    private static List<String> simpleLocations = new ArrayList<>();

    private GlobalLocationCache() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void initSimpleLocations(List<String> locations) {
        initSimpleLocations(locations.toArray(new String[0]));
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

    public static boolean containLocationValue(String location) {
        return simpleLocations.contains(location);
    }

    public static boolean containsLocation(String key) {
        return key.contains("location");
    }
}
