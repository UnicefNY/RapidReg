package org.unicef.rapidreg.service;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.CasePhotoCache;
import org.unicef.rapidreg.service.cache.SubformCache;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CaseService {
    public static final String TAG = CaseService.class.getSimpleName();
    public static final String CASE_ID = "Case ID";
    public static final String AGE = "Age";
    public static final String FULL_NAME = "Full Name";
    public static final String FIRST_NAME = "First Name";
    public static final String MIDDLE_NAME = "Middle Name";
    public static final String SURNAME = "Surname";
    public static final String NICKNAME = "Nickname";
    public static final String OTHER_NAME = "Other Name";
    public static final String CAREGIVER_NAME = "Name of Current Caregiver";
    public static final String REGISTRATION_DATE = "Date of Registration or Interview";
    public static final String CASEWORKER_CODE = "Caseworker Code";
    public static final String RECORD_CREATED_BY = "Record created by";
    public static final String PREVIOUS_OWNER = "Previous Owner";
    public static final String MODULE = "Module";

    private static final CaseService CASE_SERVICE = new CaseService();
    private CaseDao caseDao = new CaseDaoImpl();
    private CasePhotoDao casePhotoDao = new CasePhotoDaoImpl();

    public static CaseService getInstance() {
        return CASE_SERVICE;
    }

    private CaseService() {
    }

    public CaseService(CaseDao caseDao) {
        this.caseDao = caseDao;
    }

    public List<Case> getCaseList() {
        return caseDao.getAllCasesOrderByDate(false);
    }

    public List<Case> getCaseListOrderByDateASC() {
        return caseDao.getAllCasesOrderByDate(true);
    }

    public List<Case> getCaseListOrderByDateDES() {
        return caseDao.getAllCasesOrderByDate(false);
    }

    public List<Case> getCaseListOrderByAgeASC() {
        return caseDao.getAllCasesOrderByAge(true);
    }

    public List<Case> getCaseListOrderByAgeDES() {
        return caseDao.getAllCasesOrderByAge(false);
    }

    public Map<String, String> getCaseMapByUniqueId(String uniqueId) {
        Case child = caseDao.getCaseByUniqueId(uniqueId);
        if (child == null) {
            return new HashMap<>();
        }
        String caseJson = new String(child.getContent().getBlob());
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> values = new Gson().fromJson(caseJson, type);

        values.put(CASE_ID, uniqueId);

        return values;
    }

    public List<Case> getSearchResult(String uniqueId, String name, int ageFrom, int ageTo,
                                      String caregiver, Date date) {

        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(Case.COLUMN_UNIQUE_ID).build())
                .like(getWrappedCondition(uniqueId)));
        conditionGroup.and(Condition.column(NameAlias.builder(Case.COLUMN_NAME).build())
                .like(getWrappedCondition(name)));
        conditionGroup.and(Condition.column(NameAlias.builder(Case.COLUMN_AGE).build())
                .between(ageFrom).and(ageTo));
        conditionGroup.and(Condition.column(NameAlias.builder(Case.COLUMN_CAREGIVER).build())
                .like(getWrappedCondition(caregiver)));

        if (date != null) {
            conditionGroup.and(Condition.column(NameAlias.builder(Case.COLUMN_REGISTRATION_DATE)
                    .build()).eq(date));
        }

        return caseDao.getCaseListByConditionGroup(conditionGroup);
    }

    public List<String> fetchRequiredFiledNames(List<CaseField> caseFields) {
        List<String> result = new ArrayList<>();
        for (CaseField field : caseFields) {
            if (field.isRequired()) {
                result.add(field.getDisplayName().get("en"));
            }
        }
        return result;
    }

    public void saveOrUpdateCase(Map<String, String> values,
                                 Map<String, List<Map<String, String>>> subformValues,
                                 Map<Bitmap, String> photoBitPaths) {

        attachSubforms(values, subformValues);

        if (values.get(CASE_ID) == null) {
            saveCase(values, subformValues, photoBitPaths);
        } else {
            Log.d(TAG, "update the existing case");
            updateCase(values, subformValues, photoBitPaths);
        }
    }

    private void saveCase(Map<String, String> values,
                          Map<String, List<Map<String, String>>> subformValues,
                          Map<Bitmap, String> photoBitPaths) {

        String username = UserService.getInstance().getCurrentUser().getUsername();
        values.put(MODULE, "primeromodule-cp");
        values.put(CASEWORKER_CODE, username);
        values.put(RECORD_CREATED_BY, username);
        values.put(PREVIOUS_OWNER, username);

        Gson gson = new Gson();
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob caseBlob = new Blob(gson.toJson(values).getBytes());
        Blob subformBlob = new Blob(gson.toJson(subformValues).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Case child = new Case();
        child.setUniqueId(createUniqueId());
        child.setCreateDate(date);
        child.setLastUpdatedDate(date);
        child.setContent(caseBlob);
        child.setName(getChildName(values));
        int age = values.get(AGE) != null ? Integer.parseInt(values.get(AGE)) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(values));
        child.setRegistrationDate(getRegisterDate(values));
        child.setAudio(audioFileDefault);
        child.setSubform(subformBlob);
        child.setCreatedBy(username);
        child.save();

        saveCasePhoto(child, photoBitPaths);
        CaseFieldValueCache.clearAudioFile();
    }

    public void clearCaseCache() {
        CaseFieldValueCache.clear();
        CasePhotoCache.clear();
        SubformCache.clear();
    }

    public String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    private void updateCase(Map<String, String> values,
                            Map<String, List<Map<String, String>>> subformValues,
                            Map<Bitmap, String> photoBitPaths) {
        Gson gson = new Gson();
        Blob caseBlob = new Blob(gson.toJson(values).getBytes());
        Blob subformBlob = new Blob(gson.toJson(subformValues).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Case child = caseDao.getCaseByUniqueId(values.get(CASE_ID));
        child.setLastUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        child.setContent(caseBlob);
        child.setName(getChildName(values));
        int age = values.get(AGE) != null ? Integer.parseInt(values.get(AGE)) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(values));
        child.setRegistrationDate(getRegisterDate(values));
        child.setAudio(audioFileDefault);
        child.setSubform(subformBlob);
        child.update();

        casePhotoDao.deleteCasePhotosByCaseId(child.getId());
        CaseFieldValueCache.clearAudioFile();
        saveCasePhoto(child, photoBitPaths);
    }

    private Blob getAudioBlob(Blob blob) {
        try {
            blob = new Blob(StreamUtil.readFile(CaseFieldValueCache.AUDIO_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blob;
    }

    private void saveCasePhoto(Case child, Map<Bitmap, String> photoBitPaths) {
        if (photoBitPaths == null) {
            return;
        }

        for (Map.Entry<Bitmap, String> photoBitPathEntry : photoBitPaths.entrySet()) {
            try {
                CasePhoto casePhoto = new CasePhoto();
                String filePath = photoBitPathEntry.getValue();
                casePhoto.setPath(filePath);

                Bitmap bitmap = ImageCompressUtil.compressImage(filePath,
                        CasePhotoCache.MAX_WIDTH, CasePhotoCache.MAX_HEIGHT,
                        CasePhotoCache.MAX_SIZE_KB);

                byte[] imageToBytes = ImageCompressUtil.convertImageToBytes(bitmap);

                casePhoto.setPhoto(new Blob(imageToBytes));
                casePhoto.setThumbnail(new Blob(ImageCompressUtil.convertImageToBytes(photoBitPathEntry.getKey())));
                casePhoto.setCase(child);
                casePhoto.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        CasePhotoCache.clearLocalCachedPhotoFiles();
    }

    private Date getCurrentDate() {
        return new Date(Calendar.getInstance().getTimeInMillis());
    }

    private Date getRegisterDate(Map<String, String> values) {
        if (values.containsKey(REGISTRATION_DATE)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                java.util.Date date = simpleDateFormat.parse(values.get(REGISTRATION_DATE));
                return new Date(date.getTime());
            } catch (ParseException e) {
                Log.e(TAG, "date format error");
            }
        }

        return getCurrentDate();
    }

    private String getChildName(Map<String, String> values) {
        return values.get(FULL_NAME) + " "
                + values.get(FIRST_NAME) + " "
                + values.get(MIDDLE_NAME) + " "
                + values.get(SURNAME) + " "
                + values.get(NICKNAME) + " "
                + values.get(OTHER_NAME);
    }

    private String getCaregiverName(Map<String, String> values) {
        return "" + values.get(CAREGIVER_NAME);
    }

    private String getWrappedCondition(String queryStr) {
        return "%" + queryStr + "%";
    }

    private void attachSubforms(Map<String, String> values, Map<String, List<Map<String, String>>> subformValues) {
        Gson gson = new Gson();

        for (String key : subformValues.keySet()) {
            values.put(key, gson.toJson(subformValues.get(key)));
        }
    }

}
