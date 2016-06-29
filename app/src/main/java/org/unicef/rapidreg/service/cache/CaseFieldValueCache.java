package org.unicef.rapidreg.service.cache;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CaseFieldValueCache {

    public static class CaseProfile {
        public static final String ID_NORMAL_STATE = "_id_normal_state";
        public static final String SEX = "_sex";
        public static final String GENDER_NAME = "_gender_name";
        public static final String AGE = "_age";
        public static final String REGISTRATION_DATE = "_registration_date";
        public static final String ID = "_id";
    }

    public static final String AUDIO_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.3gp";

    private static Map<String, String> caseProfile = new HashMap<>();

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
        caseProfile.clear();
    }

    public static void clearAudioFile() {
        new File(AUDIO_FILE_PATH).delete();
    }

    public static void addProfileItem(String key, String value) {
        caseProfile.put(key, value);
    }

    public static String getProfileValue(String key){
        return caseProfile.get(key);
    }
}
