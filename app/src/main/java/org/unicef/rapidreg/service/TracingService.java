package org.unicef.rapidreg.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;

import org.unicef.rapidreg.db.TracingDao;
import org.unicef.rapidreg.db.TracingPhotoDao;
import org.unicef.rapidreg.db.impl.TracingDaoImpl;
import org.unicef.rapidreg.db.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class TracingService extends RecordService {
    public static final String TAG = TracingService.class.getSimpleName();
    public static final String TRACING_DISPLAY_ID = "tracing_request_id_display";
    public static final String TRACING_ID = "tracing_request_id";
    public static final String TRACING_PRIMARY_ID = "tracing_primary_id";

    private static final TracingService TRACING_SERVICE = new TracingService();

    private TracingDao tracingDao = new TracingDaoImpl();
    private TracingPhotoDao tracingPhotoDao = new TracingPhotoDaoImpl();

    public static TracingService getInstance() {
        return TRACING_SERVICE;
    }

    private TracingService() {
    }

    public TracingService(TracingDao tracingDao) {
        this.tracingDao = tracingDao;
    }

    public Tracing getById(long caseId) {
        return tracingDao.getTracingById(caseId);
    }

    public Tracing getByUniqueId(String uniqueId) {
        return tracingDao.getTracingByUniqueId(uniqueId);
    }

    public List<Tracing> getAll() {
        return tracingDao.getAllTracingsOrderByDate(false);
    }

    public List<Tracing> getAllOrderByDateASC() {
        return tracingDao.getAllTracingsOrderByDate(true);
    }

    public List<Tracing> getAllOrderByDateDES() {
        return tracingDao.getAllTracingsOrderByDate(false);
    }

    public List<Tracing> getSearchResult(String uniqueId, String name, int ageFrom, int ageTo, Date date) {
        ConditionGroup searchCondition = getSearchCondition(uniqueId, name, ageFrom, ageTo, date);

        return tracingDao.getAllTracingsByConditionGroup(searchCondition);
    }

    private ConditionGroup getSearchCondition(String uniqueId, String name, int ageFrom, int ageTo, Date date) {
        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_UNIQUE_ID).build())
                .like(getWrappedCondition(uniqueId)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_NAME).build())
                .like(getWrappedCondition(name)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_AGE).build())
                .between(ageFrom).and(ageTo));

        if (date != null) {
            conditionGroup.and(Condition.column(
                    NameAlias.builder(RecordModel.COLUMN_REGISTRATION_DATE).build()).eq(date));
        }
        return conditionGroup;
    }

    public Tracing saveOrUpdate(ItemValues itemValues, List<String> photoPaths) throws IOException {

        if (itemValues.getAsString(TRACING_ID) == null) {
            return save(itemValues, photoPaths);
        } else {
            Log.d(TAG, "update the existing tracing request");
            return update(itemValues, photoPaths);
        }
    }

    public Tracing save(ItemValues itemValues, List<String> photoPath) throws IOException {
        String uniqueId = createUniqueId();
        itemValues.addStringItem(TRACING_DISPLAY_ID, getShortUUID(uniqueId));
        itemValues.addStringItem(TRACING_ID, uniqueId);
        String username = UserService.getInstance().getCurrentUser().getUsername();
        itemValues.addStringItem(MODULE, "primeromodule-cp");
        itemValues.addStringItem(CASEWORKER_CODE, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        if (!itemValues.has(INQUIRY_DATE)) {
            itemValues.addStringItem(INQUIRY_DATE, getCurrentRegistrationDateAsString());
        }

        Gson gson = new Gson();
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob tracingBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Tracing tracing = new Tracing();
        tracing.setUniqueId(uniqueId);
        tracing.setCreateDate(date);
        tracing.setLastUpdatedDate(date);
        tracing.setContent(tracingBlob);
        tracing.setName(getName(itemValues));
        int age = itemValues.getAsInt(RELATION_AGE) != null ? itemValues.getAsInt(RELATION_AGE) : 0;
        tracing.setAge(age);
        tracing.setCaregiver(getCaregiverName(itemValues));
        tracing.setRegistrationDate(getRegisterDate(itemValues.getAsString(INQUIRY_DATE)));
        tracing.setAudio(audioFileDefault);
        tracing.setCreatedBy(username);
        tracing.save();

        savePhoto(tracing, photoPath);

        return tracing;
    }

    public void savePhoto(Tracing parent, List<String> photoPaths) throws IOException {
        for (int i = 0; i < photoPaths.size(); i++) {
            TracingPhoto tracingPhoto = generateSavePhoto(parent, photoPaths, i);
            tracingPhoto.setKey(UUID.randomUUID().toString());
            tracingPhoto.save();
        }
    }

    public Tracing update(ItemValues itemValues,
                       List<String> photoBitPaths) throws IOException {
        Gson gson = new Gson();
        Blob blob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Tracing tracing = tracingDao.getTracingByUniqueId(itemValues.getAsString(TRACING_ID));
        tracing.setLastUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        tracing.setContent(blob);
        tracing.setName(getName(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        tracing.setAge(age);
        tracing.setCaregiver(getCaregiverName(itemValues));
        tracing.setRegistrationDate(getRegisterDate(itemValues.getAsString(INQUIRY_DATE)));
        tracing.setAudio(audioFileDefault);
        tracing.update();
        updatePhoto(tracing, photoBitPaths);

        return tracing;
    }

    public void updatePhoto(Tracing tracing, List<String> photoPaths) throws IOException {
        int previousCount = tracingPhotoDao.getByTracingId(tracing.getId()).size();

        if (previousCount < photoPaths.size()) {
            for (int i = 0; i < previousCount; i++) {
                TracingPhoto TracingPhoto = generateUpdatePhoto(tracing, photoPaths, i);
                TracingPhoto.update();
            }
            for (int i = previousCount; i < photoPaths.size(); i++) {
                TracingPhoto TracingPhoto = generateSavePhoto(tracing, photoPaths, i);
                if (TracingPhoto.getId() == 0) {
                    TracingPhoto.save();
                } else {
                    TracingPhoto.update();
                }
            }
        } else {
            for (int i = 0; i < photoPaths.size(); i++) {
                TracingPhoto TracingPhoto = generateUpdatePhoto(tracing, photoPaths, i);
                TracingPhoto.update();
            }
            for (int i = photoPaths.size(); i < previousCount; i++) {
                TracingPhoto TracingPhoto =
                        tracingPhotoDao.getByTracingIdAndOrder(tracing.getId(), i + 1);
                TracingPhoto.setPhoto(null);
                TracingPhoto.update();
            }
        }
    }

    private TracingPhoto generateSavePhoto(Tracing parent, List<String> photoPaths, int index) throws IOException {

        TracingPhoto tracingPhoto
                = tracingPhotoDao.getByTracingIdAndOrder(parent.getId(), index + 1);
        if (tracingPhoto == null) {
            tracingPhoto = new TracingPhoto();
        }
        String filePath = photoPaths.get(index);
        tracingPhoto.setPhoto(ImageCompressUtil.readImageFile(filePath));
        tracingPhoto.setTracing(parent);
        tracingPhoto.setOrder(index + 1);
        tracingPhoto.setKey(UUID.randomUUID().toString());
        return tracingPhoto;
    }

    @NonNull
    private TracingPhoto generateUpdatePhoto(Tracing tracing, List<String> photoPaths, int index) throws IOException {
        TracingPhoto tracingPhoto;
        String filePath = photoPaths.get(index);
        try {
            long photoId = Long.parseLong(filePath);
            tracingPhoto = tracingPhotoDao.getById(photoId);
        } catch (NumberFormatException e) {
            tracingPhoto = new TracingPhoto();
            tracingPhoto.setTracing(tracing);
            tracingPhoto.setPhoto(ImageCompressUtil.readImageFile(filePath));
        }
        tracingPhoto.setId(tracingPhotoDao
                .getByTracingIdAndOrder(tracing.getId(), index + 1).getId());
        tracingPhoto.setOrder(index + 1);
        tracingPhoto.setKey(UUID.randomUUID().toString());
        return tracingPhoto;
    }

    private Blob getAudioBlob(Blob blob) {
        try {
            blob = new Blob(StreamUtil.readFile(AUDIO_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blob;
    }

    private String getName(ItemValues values) {
        return values.getAsString(RELATION_NAME) + " "
                + values.getAsString(RELATION_AGE) + " "
                + values.getAsString(RELATION_NICKNAME);
    }

    public Tracing getByInternalId(String id) {
        return tracingDao.getByInternalId(id);
    }

    public boolean hasSameRev(String id, String rev) {
        Tracing tracing = tracingDao.getByInternalId(id);
        return tracing != null && rev.equals(tracing.getInternalRev());
    }
}
