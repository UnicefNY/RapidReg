package org.unicef.rapidreg.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;

import org.unicef.rapidreg.childcase.config.CasePhotoConfig;
import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.cache.ItemValues;
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
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CaseService {
    public static final String TAG = CaseService.class.getSimpleName();
    public static final String CASE_ID = "case_id";
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

    public static final String AUDIO_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.3gp";

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

    public static void clearAudioFile() {
        File file = new File(AUDIO_FILE_PATH);
        file.delete();
    }

    public List<Case> getCaseList() {
        return caseDao.getAllCasesOrderByDate(false);
    }

    public Case getCaseById(long caseId) {
        return caseDao.getCaseById(caseId);
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
                result.add(field.getDisplayName().get(Locale.getDefault().getLanguage()));
            }
        }
        return result;
    }

    public void saveOrUpdateCase(ItemValues itemValues, List<String> photoPaths) {

        if (itemValues.getAsString(CASE_ID) == null) {
            saveCase(itemValues, photoPaths);
        } else {
            Log.d(TAG, "update the existing case");
            updateCase(itemValues, photoPaths);
        }
    }

    public void saveCase(ItemValues itemValues,
                         List<String> photoPath) {

        String username = UserService.getInstance().getCurrentUser().getUsername();
        itemValues.addStringItem(MODULE, "primeromodule-cp");
        itemValues.addStringItem(CASEWORKER_CODE, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        Gson gson = new Gson();
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob caseBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob subFormBlob = new Blob(gson.toJson(itemValues.getChildrenAsJsonObject()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Case child = new Case();
        child.setUniqueId(createUniqueId());
        child.setCreateDate(date);
        child.setLastUpdatedDate(date);
        child.setContent(caseBlob);
        child.setName(getChildName(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(itemValues));
        child.setRegistrationDate(getRegisterDate(itemValues));
        child.setAudio(audioFileDefault);
        child.setSubform(subFormBlob);
        child.setCreatedBy(username);
        child.save();

        try {
            saveCasePhoto(child, photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearAudioFile();
    }

    public void saveCasePhoto(Case child, List<String> photoPaths) throws IOException {
        for (int i = 0; i < photoPaths.size(); i++) {
            generateSaveCasePhoto(child, photoPaths, i).save();
        }
    }

    public void updateCase(ItemValues itemValues,
                           List<String> photoBitPaths) {
        Gson gson = new Gson();
        Blob caseBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob subFormBlob = new Blob(gson.toJson(itemValues.getChildrenAsJsonObject()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Case child = caseDao.getCaseByUniqueId(itemValues.getAsString(CASE_ID));
        child.setLastUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        child.setContent(caseBlob);
        child.setName(getChildName(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        child.setAge(age);
        child.setCaregiver(getCaregiverName(itemValues));
        child.setRegistrationDate(getRegisterDate(itemValues));
        child.setAudio(audioFileDefault);
        child.setSubform(subFormBlob);
        child.update();
        try {
            updateCasePhoto(child, photoBitPaths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearAudioFile();
    }

    public void updateCasePhoto(Case child, List<String> photoPaths) throws IOException {
        int previousCount = casePhotoDao.getAllCasesPhotoFlowQueryList(child.getId()).size();

        if (previousCount < photoPaths.size()) {
            for (int i = 0; i < previousCount; i++) {
                CasePhoto casePhoto = generateUpdateCasePhoto(child, photoPaths, i);
                casePhoto.update();
            }
            for (int i = previousCount; i < photoPaths.size(); i++) {
                CasePhoto casePhoto = generateSaveCasePhoto(child, photoPaths, i);
                if (casePhoto.getId() == 0) {
                    casePhoto.save();
                } else {
                    casePhoto.update();
                }
            }
        } else {
            for (int i = 0; i < photoPaths.size(); i++) {
                CasePhoto casePhoto = generateUpdateCasePhoto(child, photoPaths, i);
                casePhoto.update();
            }
            for (int i = photoPaths.size(); i < previousCount; i++) {
                CasePhoto casePhoto = casePhotoDao.getSpecialOrderCasePhotoByCaseId(child.getId(), i + 1);
                casePhoto.setPhoto(null);
                casePhoto.setThumbnail(null);
                casePhoto.update();
            }
        }
    }

    public ItemValues generateItemValues(String parentJson, String childJson) {
        final JsonObject caseInfo = new Gson().fromJson(parentJson, JsonObject.class);
        final JsonObject subFormInfo = new Gson().fromJson(childJson, JsonObject.class);
        ItemValues itemValues = new ItemValues(caseInfo);
        for (Map.Entry<String, JsonElement> element : subFormInfo.entrySet()) {
            itemValues.addChildren(element.getKey(), element.getValue().getAsJsonArray());
        }
        return itemValues;
    }

    public String getShortUUID(String uuid) {
        int length = uuid.length();
        return length > 7 ? uuid.substring(length - 7) : uuid;
    }

    private CasePhoto generateSaveCasePhoto(Case child, List<String> photoPaths, int index) throws IOException {
        CasePhoto casePhoto = casePhotoDao.getSpecialOrderCasePhotoByCaseId(child.getId(), index + 1);
        if (casePhoto == null) {
            casePhoto = new CasePhoto();
        }
        String filePath = photoPaths.get(index);
        Bitmap bitmap = preProcessImage(filePath);
        casePhoto.setThumbnail(new Blob(ImageCompressUtil.convertImageToBytes(
                ImageCompressUtil.getThumbnail(bitmap, CasePhotoConfig.THUMBNAIL_SIZE,
                        CasePhotoConfig.THUMBNAIL_SIZE))));

        casePhoto.setPhoto(new Blob(ImageCompressUtil.convertImageToBytes(bitmap)));
        casePhoto.setCase(child);
        casePhoto.setOrder(index + 1);
        return casePhoto;
    }

    @NonNull
    private CasePhoto generateUpdateCasePhoto(Case child, List<String> photoPaths, int index)
            throws IOException {
        CasePhoto casePhoto;
        String filePath = photoPaths.get(index);
        Blob photo;
        try {
            long photoId = Long.parseLong(filePath);
            casePhoto = casePhotoDao.getCasePhotoById(photoId);
        } catch (NumberFormatException e) {
            Bitmap bitmap = preProcessImage(filePath);
            photo = new Blob(ImageCompressUtil.convertImageToBytes(bitmap));
            casePhoto = new CasePhoto();
            casePhoto.setThumbnail(new Blob(ImageCompressUtil.convertImageToBytes(
                    ImageCompressUtil.getThumbnail(bitmap, CasePhotoConfig.THUMBNAIL_SIZE,
                            CasePhotoConfig.THUMBNAIL_SIZE))));
            casePhoto.setCase(child);
            casePhoto.setPhoto(photo);
        }
        casePhoto.setId(casePhotoDao.getSpecialOrderCasePhotoByCaseId(child.getId(), index + 1).getId());
        casePhoto.setOrder(index + 1);
        return casePhoto;
    }

    private Bitmap preProcessImage(String filePath) throws IOException {
        if (new File(filePath).length() <= 1024 * 1024 * 1) {
            return BitmapFactory.decodeFile(filePath);
        }
        return ImageCompressUtil.compressImage(filePath,
                CasePhotoConfig.MAX_WIDTH, CasePhotoConfig.MAX_HEIGHT);
    }

    public String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    private Blob getAudioBlob(Blob blob) {
        try {
            blob = new Blob(StreamUtil.readFile(AUDIO_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blob;
    }

    private Date getCurrentDate() {
        return new Date(Calendar.getInstance().getTimeInMillis());
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

    private String getChildName(ItemValues values) {
        return values.getAsString(FULL_NAME) + " "
                + values.getAsString(FIRST_NAME) + " "
                + values.getAsString(MIDDLE_NAME) + " "
                + values.getAsString(SURNAME) + " "
                + values.getAsString(NICKNAME) + " "
                + values.getAsString(OTHER_NAME);
    }

    private String getCaregiverName(ItemValues itemValues) {
        return "" + itemValues.getAsString(CAREGIVER_NAME);
    }

    private String getWrappedCondition(String queryStr) {
        return "%" + queryStr + "%";
    }
}
