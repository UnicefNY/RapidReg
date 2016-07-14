package org.unicef.rapidreg.service;

import android.os.Environment;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RecordService {
    public static final String RECORD_ID = "record_id";
    public static final String ITEM_VALUES = "item_values";
    public static final String RECORD_PHOTOS = "record_photos";
    public static final String AGE = "age";
    public static final String FULL_NAME = "name";
    public static final String FIRST_NAME = "name_first";
    public static final String MIDDLE_NAME = "name_middle";
    public static final String SURNAME = "name_last";
    public static final String NICKNAME = "name_nickname";
    public static final String OTHER_NAME = "name_other";
    public static final String CAREGIVER_NAME = "name_caregiver";
    public static final String REGISTRATION_DATE = "registration_date";
    public static final String CASEWORKER_CODE = "owned_by";
    public static final String RECORD_CREATED_BY = "created_by";
    public static final String PREVIOUS_OWNER = "previously_owned_by";
    public static final String MODULE = "module_id";

    public static final String AUDIO_FILE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/audiorecordtest.3gp";

    public static RecordService getInstance() {
        return new RecordService();
    }

    public static void clearAudioFile() {
        File file = new File(AUDIO_FILE_PATH);
        file.delete();
    }

    public static String getShortUUID(String uuid) {
        int length = uuid.length();
        return length > 7 ? uuid.substring(length - 7) : uuid;
    }

    public static List<String> fetchRequiredFiledNames(List<Field> fields) {
        List<String> result = new ArrayList<>();
        for (Field field : fields) {
            if (field.isRequired()) {
                result.add(field.getDisplayName().get(Locale.getDefault().getLanguage()));
            }
        }
        return result;
    }

    public String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    protected String getName(ItemValues values) {
        return values.getAsString(FULL_NAME) + " "
                + values.getAsString(FIRST_NAME) + " "
                + values.getAsString(MIDDLE_NAME) + " "
                + values.getAsString(SURNAME) + " "
                + values.getAsString(NICKNAME) + " "
                + values.getAsString(OTHER_NAME);
    }

    protected String getCaregiverName(ItemValues itemValues) {
        return "" + itemValues.getAsString(CAREGIVER_NAME);
    }

    protected String getWrappedCondition(String queryStr) {
        return "%" + queryStr + "%";
    }

    protected Date getCurrentDate() {
        return new Date(Calendar.getInstance().getTimeInMillis());
    }
}
