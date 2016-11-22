package org.unicef.rapidreg.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

public class CaseService extends RecordService {
    public static final String TAG = CaseService.class.getSimpleName();
    public static final String CASE_DISPLAY_ID = "case_id_display";
    public static final String CASE_ID = "case_id";
    public static final String CASE_PRIMARY_ID = "case_primary_id";

    private static final CaseService CASE_SERVICE = new CaseService();

    @Inject
    UserService userService;

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

    public List<Case> getAll() {
        return caseDao.getAllCasesOrderByDate(false);
    }

    public Case getFirst() {
        return caseDao.getFirst();
    }

    public Case getById(long caseId) {
        return caseDao.getCaseById(caseId);
    }

    public List<Long> getAllOrderByDateASC() {
        return extractIds(caseDao.getAllCasesOrderByDate(true));
    }

    public List<Long> getAllOrderByDateDES() {
        return extractIds(caseDao.getAllCasesOrderByDate(false));
    }

    public List<Long> getAllOrderByAgeASC() {
        return extractIds(caseDao.getAllCasesOrderByAge(true));
    }

    public List<Long> getAllOrderByAgeDES() {
        return extractIds(caseDao.getAllCasesOrderByAge(false));
    }

    public List<Long> extractIds(List<Case> cases){
        List<Long> result = new ArrayList<>();
        for (Case aCase : cases) {
            result.add(aCase.getId());
        }
        return result;
    }

    public Case getByUniqueId(String uniqueId) {
        return caseDao.getCaseByUniqueId(uniqueId);
    }

    public List<Long> getSearchResult(String shortId, String name, int ageFrom, int ageTo,
                                      String caregiver, Date date) {
        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_SHORT_ID).build())
                .like(getWrappedCondition(shortId)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_NAME).build())
                .like(getWrappedCondition(name)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_AGE).build())
                .between(ageFrom).and(ageTo));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_CAREGIVER).build())
                .like(getWrappedCondition(caregiver)));

        if (date != null) {
            conditionGroup.and(Condition.column(NameAlias.builder(Case.COLUMN_REGISTRATION_DATE)
                    .build()).eq(date));
        }

        return extractIds(caseDao.getCaseListByConditionGroup(conditionGroup));
    }

    public Case saveOrUpdate(ItemValues itemValues, List<String> photoPaths) throws IOException {
        if (itemValues.getAsString(CASE_ID) == null) {
            return save(itemValues, photoPaths);
        } else {
            Log.d(TAG, "update the existing case");
            return update(itemValues, photoPaths);
        }
    }

    public List<Long> getAllIds() {
        return caseDao.getAllIds();
    }

    public Case save(ItemValues itemValues, List<String> photoPath) throws IOException {
        String uniqueId = createUniqueId();
        String username = userService.getCurrentUser().getUsername();
        itemValues.addStringItem(CASE_DISPLAY_ID, getShortUUID(uniqueId));
        itemValues.addStringItem(CASE_ID, uniqueId);
        itemValues.addStringItem(MODULE, "primeromodule-cp");
        itemValues.addStringItem(CASEWORKER_CODE, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        if (!itemValues.has(REGISTRATION_DATE)) {
            itemValues.addStringItem(REGISTRATION_DATE, getCurrentRegistrationDateAsString());
        }

        Gson gson = new Gson();
        Date date = new Date(System.currentTimeMillis());
        Blob blob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Case child = new Case();
        child.setUniqueId(uniqueId);
        // Now, short_id is generate by last 7 charactors of case_id as server. If server change the
        // short_id generation rule, there should be changed.
        child.setShortId(getShortUUID(uniqueId));
        child.setCreateDate(date);
        child.setLastUpdatedDate(date);
        child.setContent(blob);
        child.setName(getName(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(itemValues));
        child.setRegistrationDate(getRegisterDate(itemValues.getAsString(REGISTRATION_DATE)));
        child.setAudio(audioFileDefault);
        child.setCreatedBy(username);
        child.save();

        savePhoto(child, photoPath);

        return child;
    }

    public void savePhoto(Case child, List<String> photoPaths) throws IOException {
        for (int i = 0; i < photoPaths.size(); i++) {
            CasePhoto casePhoto = generateSavePhoto(child, photoPaths, i);
            casePhoto.setKey(UUID.randomUUID().toString());
            casePhoto.save();
        }
    }

    public Case update(ItemValues itemValues, List<String> photoBitPaths) throws IOException {
        Gson gson = new Gson();
        Blob caseBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Case child = caseDao.getCaseByUniqueId(itemValues.getAsString(CASE_ID));
        child.setLastUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        child.setContent(caseBlob);
        child.setName(getName(itemValues));
        setSyncedStatus(child);
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(itemValues));
        child.setRegistrationDate(getRegisterDate(itemValues.getAsString(REGISTRATION_DATE)));
        child.setAudio(audioFileDefault);
        child.update();
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

    private CasePhoto generateSavePhoto(Case child, List<String> photoPaths, int index) throws IOException {
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
    private CasePhoto generateUpdatePhoto(Case child, List<String> photoPaths, int index) throws IOException {
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

    private Blob getAudioBlob(Blob blob) {
        if (StreamUtil.isFileExists(AUDIO_FILE_PATH)) {
            try {
                blob = new Blob(StreamUtil.readFile(AUDIO_FILE_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return blob;
    }


    private String getName(ItemValues values) {
        return values.getAsString(FULL_NAME) + " "
                + values.getAsString(FIRST_NAME) + " "
                + values.getAsString(MIDDLE_NAME) + " "
                + values.getAsString(SURNAME) + " "
                + values.getAsString(NICKNAME) + " "
                + values.getAsString(OTHER_NAME);
    }

    public Case getByInternalId(String id) {
        return caseDao.getByInternalId(id);
    }

    public boolean hasSameRev(String id, String rev) {
        Case aCase = caseDao.getByInternalId(id);
        return aCase != null && rev.equals(aCase.getInternalRev());
    }
}
