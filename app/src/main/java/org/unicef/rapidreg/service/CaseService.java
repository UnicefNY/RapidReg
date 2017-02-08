package org.unicef.rapidreg.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.repository.CaseDao;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.repository.IncidentDao;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.utils.StreamUtil;
import org.unicef.rapidreg.utils.TextUtils;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;

public class CaseService extends RecordService {
    public static final String TAG = CaseService.class.getSimpleName();
    public static final String CASE_DISPLAY_ID = "case_id_display";
    public static final String CASE_ID = "case_id";
    public static final String CASE_PRIMARY_ID = "case_primary_id";

    private CaseDao caseDao;
    private CasePhotoDao casePhotoDao;
    private IncidentDao incidentDao;

    public CaseService(CaseDao caseDao, CasePhotoDao casePhotoDao, IncidentDao incidentDao) {
        this.caseDao = caseDao;
        this.casePhotoDao = casePhotoDao;
        this.incidentDao = incidentDao;
    }

    public List<Case> getAll() {
        return caseDao.getAllCasesOrderByDate(false, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl()));
    }

    public Case getById(long caseId) {
        return caseDao.getCaseById(caseId);
    }

    public Case getByUniqueId(String uniqueId) {
        return caseDao.getCaseByUniqueId(uniqueId);
    }

    public List<String> getIncidentsByCaseId(String caseUniqueId) {
        List<Incident> incidents = incidentDao.getAllIncidentsByCaseUniqueId(caseUniqueId);
        if (incidents == null || incidents.isEmpty()) {
            return null;
        }
        return extractUniqueIds(incidents);
    }

    public List<Long> getAllOrderByDateASC() {
        return extractIds(caseDao.getAllCasesOrderByDate(true, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl())));
    }

    public List<Long> getAllOrderByDateDES() {
        return extractIds(caseDao.getAllCasesOrderByDate(false, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl())));
    }

    public List<Long> getAllOrderByAgeASC() {
        return extractIds(caseDao.getAllCasesOrderByAge(true, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl())));
    }

    public List<Long> getAllOrderByAgeDES() {
        return extractIds(caseDao.getAllCasesOrderByAge(false, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl())));
    }

    public List<Long> getCPSearchResult(String shortId, String name, int ageFrom, int ageTo,
                                        String caregiver, Date date) {
        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_SHORT_ID).build())
                .like(getWrappedCondition(shortId)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_NAME).build())
                .like(getWrappedCondition(name)));
        SQLCondition ageSearchCondition = generateAgeSearchCondition(ageFrom, ageTo);
        if (ageSearchCondition != null) {
            conditionGroup.and(ageSearchCondition);
        }

        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_CAREGIVER).build())
                .like(getWrappedCondition(caregiver)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_OWNED_BY).build())
                .eq(PrimeroAppConfiguration.getCurrentUser().getUsername()));

        if (date != null) {
            conditionGroup.and(Condition.column(NameAlias.builder(Case.COLUMN_REGISTRATION_DATE)
                    .build()).eq(date));
        }

        return extractIds(caseDao.getCaseListByConditionGroup(PrimeroAppConfiguration.getCurrentUsername(),
                PrimeroAppConfiguration.getApiBaseUrl(),
                conditionGroup));
    }

    public List<Long> getGBVSearchResult(String shortId, String name, String location, Date registrationDate) {
        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_SHORT_ID).build())
                .like(getWrappedCondition(shortId)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_NAME).build())
                .like(getWrappedCondition(name)));
//      TODO  conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_LOCATION).build())
//                .like(getWrappedCondition(location)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_OWNED_BY).build())
                .eq(PrimeroAppConfiguration.getCurrentUser().getUsername()));

        if (registrationDate != null) {
            conditionGroup.and(Condition.column(NameAlias.builder(Case.COLUMN_REGISTRATION_DATE)
                    .build()).eq(registrationDate));
        }

        return extractIds(caseDao.getCaseListByConditionGroup(PrimeroAppConfiguration.getCurrentUsername(),
                PrimeroAppConfiguration.getApiBaseUrl(), conditionGroup));
    }

    public Case saveOrUpdate(ItemValuesMap itemValues, List<String> photoPaths) throws IOException {
        if (itemValues.getAsString(CASE_ID) == null) {
            return save(itemValues, photoPaths);
        } else {
            Log.d(TAG, "update the existing case");
            return update(itemValues, photoPaths);
        }
    }

    public Case save(ItemValuesMap itemValues, List<String> photoPaths) throws IOException {
        String uniqueId = generateUniqueId();
        String username = PrimeroAppConfiguration.getCurrentUser().getUsername();
        itemValues.addStringItem(CASE_DISPLAY_ID, getShortUUID(uniqueId));
        itemValues.addStringItem(CASE_ID, uniqueId);
        itemValues.addStringItem(RECORD_OWNED_BY, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        if (!itemValues.has(REGISTRATION_DATE)) {
            String registrationDateVal = getCurrentRegistrationDateAsString();
            itemValues.addStringItem(REGISTRATION_DATE, registrationDateVal);
            itemValues.addStringItem(CASE_OPENING_DATE, registrationDateVal);
        }

        Gson gson = new Gson();
        Date date = new Date(System.currentTimeMillis());
        Blob blob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = getAudioBlob();

        Case child = new Case();
        child.setUniqueId(uniqueId);
        child.setShortId(getShortUUID(uniqueId));
        child.setCreateDate(date);
        child.setLastUpdatedDate(date);
        child.setContent(blob);

        child.setName(getName(itemValues));

        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : EMPTY_AGE;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(itemValues));
        child.setRegistrationDate(Utils.getRegisterDate(itemValues.getAsString(REGISTRATION_DATE)));
        child.setAudio(audioFileDefault);
        child.setCreatedBy(username);
        child.setOwnedBy(username);
        child.setModuleId(itemValues.getAsString(MODULE));
        String location = itemValues.has(LOCATION) ? itemValues.getAsString(LOCATION) : "";
        child.setLocation(location);
        child.setUrl(TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl()));
        caseDao.save(child);
        savePhoto(child, photoPaths);
        return child;
    }

    public void savePhoto(Case child, List<String> photoPaths) throws IOException {
        for (int i = 0; i < photoPaths.size(); i++) {
            CasePhoto casePhoto = generateSavePhoto(child, photoPaths, i);
            casePhoto.setKey(UUID.randomUUID().toString());
            casePhotoDao.save(casePhoto);
        }
    }

    public Case update(ItemValuesMap itemValues, List<String> photoBitPaths) throws IOException {
        Gson gson = new Gson();
        Blob caseBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = getAudioBlob();

        Case child = caseDao.getCaseByUniqueId(itemValues.getAsString(CASE_ID));
        child.setLastUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        child.setContent(caseBlob);
        child.setName(getName(itemValues));
        String location = itemValues.has(LOCATION) ? itemValues.getAsString(LOCATION) : "";
        child.setLocation(location);
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(itemValues));
        child.setRegistrationDate(Utils.getRegisterDate(itemValues.getAsString(REGISTRATION_DATE)));
        child.setAudio(audioFileDefault);
        child.setSynced(false);

        child = caseDao.update(child);

        updatePhoto(child, photoBitPaths);

        return child;
    }

    public void updatePhoto(Case child, List<String> photoPaths) throws IOException {
        int previousCount = casePhotoDao.getIdsByCaseId(child.getId()).size();

        if (previousCount < photoPaths.size()) {
            for (int i = 0; i < previousCount; i++) {
                CasePhoto casePhoto = generateUpdatePhoto(child, photoPaths, i);
                casePhoto.update();
            }
            for (int i = previousCount; i < photoPaths.size(); i++) {
                CasePhoto casePhoto = generateSavePhoto(child, photoPaths, i);
                if (casePhoto.getId() == 0) {
                    casePhoto.save();
                } else {
                    casePhoto.update();
                }
            }
        } else {
            for (int i = 0; i < photoPaths.size(); i++) {
                CasePhoto casePhoto = generateUpdatePhoto(child, photoPaths, i);
                casePhoto.update();
            }
            for (int i = photoPaths.size(); i < previousCount; i++) {
                CasePhoto casePhoto = casePhotoDao.getByCaseIdAndOrder(child.getId(), i + 1);
                casePhoto.setPhoto(null);
                casePhoto.update();
            }
        }
    }

    private CasePhoto generateSavePhoto(Case child, List<String> photoPaths, int index) throws
            IOException {
        CasePhoto casePhoto = casePhotoDao.getByCaseIdAndOrder(child.getId(), index + 1);
        if (casePhoto == null) {
            casePhoto = new CasePhoto();
        }
        String filePath = photoPaths.get(index);
        Blob photo = ImageCompressUtil.readImageFile(filePath);
        casePhoto.setPhoto(photo);
        casePhoto.setCase(child);
        casePhoto.setOrder(index + 1);
        casePhoto.setKey(UUID.randomUUID().toString());
        return casePhoto;
    }

    @NonNull
    private CasePhoto generateUpdatePhoto(Case child, List<String> photoPaths, int index) throws
            IOException {
        CasePhoto casePhoto;
        String filePath = photoPaths.get(index);
        try {
            long photoId = Long.parseLong(filePath);
            casePhoto = casePhotoDao.getById(photoId);
        } catch (NumberFormatException e) {
            Blob photo = ImageCompressUtil.readImageFile(filePath);
            casePhoto = new CasePhoto();
            casePhoto.setCase(child);
            casePhoto.setPhoto(photo);
        }
        casePhoto.setId(casePhotoDao.getByCaseIdAndOrder(child.getId(), index + 1).getId());
        casePhoto.setOrder(index + 1);
        casePhoto.setKey(UUID.randomUUID().toString());
        return casePhoto;
    }

    private Blob getAudioBlob() {
        if (StreamUtil.isFileExists(AUDIO_FILE_PATH)) {
            try {
                return new Blob(StreamUtil.readFile(AUDIO_FILE_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private String getName(ItemValuesMap values) {
        return values.concatMultiStringsWithBlank(FULL_NAME, FIRST_NAME, MIDDLE_NAME, SURNAME, NICKNAME, OTHER_NAME);
    }

    public Case getByInternalId(String id) {
        return caseDao.getByInternalId(id);
    }

    public boolean hasSameRev(String id, String rev) {
        Case aCase = caseDao.getByInternalId(id);
        return aCase != null && rev.equals(aCase.getInternalRev());
    }
}
