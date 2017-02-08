package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;

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
    public static final String CASE_OPENING_DATE = "created_at";
    public static final String DATE_OF_INTERVIEW = "date_of_first_report";
    public static final String RECORD_OWNED_BY = "owned_by";
    public static final String RECORD_CREATED_BY = "created_by";
    public static final String PREVIOUS_OWNER = "previously_owned_by";
    public static final String MODULE = "module_id";
    public static final String RELATION_NAME = "relation_name";
    public static final String RELATION_AGE = "relation_age";
    public static final String RELATION_NICKNAME = "relation_nickname";
    public static final String SEX = "sex";
    public static final String INQUIRY_DATE = "inquiry_date";
    public static final String SURVIVOR_CODE = "survivor_code";
    public static final String TYPE_OF_VIOLENCE = "type_of_incident_violence";
    public static final String LOCATION = "location";

    public static final String MODULE_GBV_CASE = "primeromodule-gbv";
    public static final String MODULE_CP_CASE = "primeromodule-cp";

    public static final String AUDIO_FILE_PATH = PrimeroAppConfiguration.getInternalFilePath() + "/audioFile.amr";
    private static final String TAG = RecordService.class.getSimpleName();

    public String getShortUUID(String uuid) {
        int length = uuid.length();
        return length > 7 ? uuid.substring(length - 7) : uuid;
    }

    public List<String> fetchRequiredFiledNames(List<Field> fields) {
        List<String> result = new ArrayList<>();
        for (Field field : fields) {
            if (field.isRequired()) {
                result.add(field.getName());
            }
        }
        return result;
    }

    protected String getCaregiverName(ItemValuesMap itemValues) {
        return itemValues.concatMultiStringsWithBlank(CAREGIVER_NAME);
    }

    protected String getWrappedCondition(String queryStr) {
        return "%" + queryStr + "%";
    }

    public String getCurrentRegistrationDateAsString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
    }

    public String getSurvivorCode(ItemValuesMap itemValues) {
        return "" + itemValues.getAsString(SURVIVOR_CODE);
    }

    public String getTypeOfViolence(ItemValuesMap itemValues) {
        return "" + itemValues.getAsString(TYPE_OF_VIOLENCE);
    }

    public String getLocation(ItemValuesMap itemValues) {
        return itemValues.getAsString(LOCATION);
    }

    public  String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public <T extends RecordModel> List<Long> extractIds(List<T> records) {
        List<Long> result = new ArrayList<>();
        for (RecordModel record : records) {
            result.add(record.getId());
        }
        return result;
    }

    public <T extends RecordModel> List<String> extractUniqueIds(List<T> records) {
        List<String> result = new ArrayList<>();
        for (RecordModel record : records) {
            result.add(record.getUniqueId());
        }
        return result;
    }

    public boolean validateRequiredFields(RecordForm recordForm, ItemValuesMap itemValues) {
        List<String> requiredFieldNames = new ArrayList<>();
        for (Section section : recordForm.getSections()) {
            Collections.addAll(requiredFieldNames, fetchRequiredFiledNames(section.getFields()).toArray(new String[0]));
        }
        for (String field : requiredFieldNames) {
            Object fieldValue = itemValues.getValues().get(field);
            if (fieldValue == null || fieldValue.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    protected SQLCondition generateAgeSearchCondition(int ageFrom, int ageTo) {
        if (ageFrom == EMPTY_AGE && ageTo == EMPTY_AGE) {
            return null;
        }

        if (ageFrom == EMPTY_AGE && ageTo != EMPTY_AGE) {
            return Condition.column(NameAlias.builder(RecordModel.COLUMN_AGE).build())
                    .lessThan(ageTo + 1);
        }

        if (ageFrom != EMPTY_AGE && ageTo == EMPTY_AGE) {
            return Condition.column(NameAlias.builder(RecordModel.COLUMN_AGE).build())
                    .greaterThan(ageFrom - 1);
        }

        return Condition.column(NameAlias.builder(RecordModel.COLUMN_AGE).build())
                .between(ageFrom).and(ageTo);
    }
}
