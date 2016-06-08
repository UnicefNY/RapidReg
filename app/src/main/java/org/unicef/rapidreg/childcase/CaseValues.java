package org.unicef.rapidreg.childcase;

import java.util.HashMap;
import java.util.Map;

public class CaseValues {

    private static Map<String, String> values = new HashMap<>();

    public static Map<String, String> getInstance() {
        return values;
    }

    public static void clear() {
        values.clear();
    }
}
