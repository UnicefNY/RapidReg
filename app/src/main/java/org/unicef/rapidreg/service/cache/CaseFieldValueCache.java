package org.unicef.rapidreg.service.cache;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CaseFieldValueCache {
    public static final String AUDIO_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.3gp";

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

    public static void clearAudioFile(){
        new File(AUDIO_FILE_PATH).delete();
    }
}
