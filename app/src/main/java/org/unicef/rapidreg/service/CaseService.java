package org.unicef.rapidreg.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;

import org.unicef.rapidreg.PrimeroConfiguration;
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

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CaseService extends RecordService {
    public static final String TAG = CaseService.class.getSimpleName();
    public static final String CASE_ID = "case_id_display";

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

    public List<Case> getAll() {
        return caseDao.getAllCasesOrderByDate(false);
    }

    public Case getById(long caseId) {
        return caseDao.getCaseById(caseId);
    }

    public List<Case> getAllOrderByDateASC() {
        return caseDao.getAllCasesOrderByDate(true);
    }

    public List<Case> getAllOrderByDateDES() {
        return caseDao.getAllCasesOrderByDate(false);
    }

    public List<Case> getAllOrderByAgeASC() {
        return caseDao.getAllCasesOrderByAge(true);
    }

    public List<Case> getAllOrderByAgeDES() {
        return caseDao.getAllCasesOrderByAge(false);
    }

    public Case getCaseByUniqueId(String uniqueId) {
        return caseDao.getCaseByUniqueId(uniqueId);
    }

    public List<Case> getSearchResult(String uniqueId, String name, int ageFrom, int ageTo,
                                      String caregiver, Date date) {
        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_UNIQUE_ID).build())
                .like(getWrappedCondition(uniqueId)));
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

        return caseDao.getCaseListByConditionGroup(conditionGroup);
    }

    public void saveOrUpdate(ItemValues itemValues, List<String> photoPaths) {

        if (itemValues.getAsString(CASE_ID) == null) {
            save(itemValues, photoPaths);
        } else {
            Log.d(TAG, "update the existing case");
            update(itemValues, photoPaths);
        }
    }

    public void save(ItemValues itemValues, List<String> photoPath) {
        String username = UserService.getInstance().getCurrentUser().getUsername();
        itemValues.addStringItem(MODULE, "primeromodule-cp");
        itemValues.addStringItem(CASEWORKER_CODE, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        Gson gson = new Gson();
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob blob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Case child = new Case();
        child.setUniqueId(createUniqueId());
        child.setCreateDate(date);
        child.setLastUpdatedDate(date);
        child.setContent(blob);
        child.setName(getName(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(itemValues));
        child.setRegistrationDate(getRegisterDate(itemValues));
        child.setAudio(audioFileDefault);
        child.setCreatedBy(username);
        child.save();

        try {
            savePhoto(child, photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearAudioFile();
    }

    public void savePhoto(Case child, List<String> photoPaths) throws IOException {
        for (int i = 0; i < photoPaths.size(); i++) {
            generateSavePhoto(child, photoPaths, i).save();
        }
    }

    public void update(ItemValues itemValues, List<String> photoBitPaths) {
        Gson gson = new Gson();
        Blob caseBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Case child = caseDao.getCaseByUniqueId(itemValues.getAsString(CASE_ID));
        child.setLastUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        child.setContent(caseBlob);
        child.setName(getName(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(itemValues));
        child.setRegistrationDate(getRegisterDate(itemValues));
        child.setAudio(audioFileDefault);
        child.update();
        try {
            updatePhoto(child, photoBitPaths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearAudioFile();
    }

    public void updatePhoto(Case child, List<String> photoPaths) throws IOException {
        int previousCount = casePhotoDao.getByCaseId(child.getId()).size();

        if (previousCount < photoPaths.size()) {
            for (int i = 0; i < previousCount; i++) {
                CasePhoto CasePhoto = generateUpdatePhoto(child, photoPaths, i);
                CasePhoto.update();
            }
            for (int i = previousCount; i < photoPaths.size(); i++) {
                CasePhoto CasePhoto = generateSavePhoto(child, photoPaths, i);
                if (CasePhoto.getId() == 0) {
                    CasePhoto.save();
                } else {
                    CasePhoto.update();
                }
            }
        } else {
            for (int i = 0; i < photoPaths.size(); i++) {
                CasePhoto CasePhoto = generateUpdatePhoto(child, photoPaths, i);
                CasePhoto.update();
            }
            for (int i = photoPaths.size(); i < previousCount; i++) {
                CasePhoto CasePhoto = casePhotoDao.getByCaseIdAndOrder(child.getId(), i + 1);
                CasePhoto.setPhoto(null);
                CasePhoto.setThumbnail(null);
                CasePhoto.update();
            }
        }
    }

    private CasePhoto generateSavePhoto(Case child, List<String> photoPaths, int index) throws IOException {
        CasePhoto CasePhoto = casePhotoDao.getByCaseIdAndOrder(child.getId(), index + 1);
        if (CasePhoto == null) {
            CasePhoto = new CasePhoto();
        }
        String filePath = photoPaths.get(index);
        Bitmap bitmap = preProcessImage(filePath);
        CasePhoto.setThumbnail(new Blob(ImageCompressUtil.convertImageToBytes(
                ImageCompressUtil.getThumbnail(bitmap, PrimeroConfiguration.PHOTO_THUMBNAIL_SIZE,
                        PrimeroConfiguration.PHOTO_THUMBNAIL_SIZE))));

        CasePhoto.setPhoto(new Blob(ImageCompressUtil.convertImageToBytes(bitmap)));
        CasePhoto.setCase(child);
        CasePhoto.setOrder(index + 1);
        return CasePhoto;
    }

    @NonNull
    private CasePhoto generateUpdatePhoto(Case child, List<String> photoPaths, int index) throws IOException {
        CasePhoto casePhoto;
        String filePath = photoPaths.get(index);
        Blob photo;
        try {
            long photoId = Long.parseLong(filePath);
            casePhoto = casePhotoDao.getById(photoId);
        } catch (NumberFormatException e) {
            Bitmap bitmap = preProcessImage(filePath);
            photo = new Blob(ImageCompressUtil.convertImageToBytes(bitmap));
            casePhoto = new CasePhoto();
            casePhoto.setThumbnail(new Blob(ImageCompressUtil.convertImageToBytes(
                    ImageCompressUtil.getThumbnail(bitmap, PrimeroConfiguration.PHOTO_THUMBNAIL_SIZE,
                            PrimeroConfiguration.PHOTO_THUMBNAIL_SIZE))));
            casePhoto.setCase(child);
            casePhoto.setPhoto(photo);
        }
        casePhoto.setId(casePhotoDao.getByCaseIdAndOrder(child.getId(), index + 1).getId());
        casePhoto.setOrder(index + 1);
        return casePhoto;
    }

    private Bitmap preProcessImage(String filePath) throws IOException {
        if (new File(filePath).length() <= 1024 * 1024 * 1) {
            return BitmapFactory.decodeFile(filePath);
        }
        return ImageCompressUtil.compressImage(filePath,
                PrimeroConfiguration.PHOTO_MAX_WIDTH, PrimeroConfiguration.PHOTO_MAX_HEIGHT);
    }

    private Blob getAudioBlob(Blob blob) {
        try {
            blob = new Blob(StreamUtil.readFile(AUDIO_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blob;
    }

    private Date getRegisterDate(ItemValues itemValues) {
        if (itemValues.has(REGISTRATION_DATE)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                java.util.Date date = simpleDateFormat.parse(itemValues.getAsString(REGISTRATION_DATE));
                return new Date(date.getTime());
            } catch (ParseException e) {
                Log.e(TAG, "date format error");
            }
        }
        return getCurrentDate();
    }

    private String getName(ItemValues values) {
        return values.getAsString(FULL_NAME) + " "
                + values.getAsString(FIRST_NAME) + " "
                + values.getAsString(MIDDLE_NAME) + " "
                + values.getAsString(SURNAME) + " "
                + values.getAsString(NICKNAME) + " "
                + values.getAsString(OTHER_NAME);
    }
}
