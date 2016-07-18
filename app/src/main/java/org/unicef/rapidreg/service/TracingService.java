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

import org.unicef.rapidreg.base.PhotoConfig;
import org.unicef.rapidreg.db.TracingDao;
import org.unicef.rapidreg.db.TracingPhotoDao;
import org.unicef.rapidreg.db.TracingPhotoDaoImpl;
import org.unicef.rapidreg.db.impl.TracingDaoImpl;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingPhoto;
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

public class TracingService extends RecordService {
    public static final String TAG = TracingService.class.getSimpleName();
    public static final String TRACING_ID = "tracing_request_id_display";

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

    public List<Tracing> getAll() {
        return tracingDao.getAllTracingsOrderByDate(false);
    }

    public List<Tracing> getAllOrderByDateASC() {
        return tracingDao.getAllTracingsOrderByDate(true);
    }

    public List<Tracing> getAllOrderByDateDES() {
        return tracingDao.getAllTracingsOrderByDate(false);
    }


    public List<Tracing> getSearchResult(String uniqueId, String name, int ageFrom,
                                         int ageTo, String caregiver, Date date) {

        ConditionGroup searchCondition = getSearchCondition(uniqueId, name, ageFrom, ageTo, caregiver, date);

        return tracingDao.getAllTracingsByConditionGroup(searchCondition);
    }

    private ConditionGroup getSearchCondition(String uniqueId, String name, int ageFrom, int ageTo,
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
            conditionGroup.and(Condition.column(
                    NameAlias.builder(RecordModel.COLUMN_REGISTRATION_DATE).build()).eq(date));
        }

        return conditionGroup;
    }

    public void saveOrUpdate(ItemValues itemValues, List<String> photoPaths) {

        if (itemValues.getAsString(TRACING_ID) == null) {
            save(itemValues, photoPaths);
        } else {
            Log.d(TAG, "update the existing tracing request");
            update(itemValues, photoPaths);
        }
    }

    public void save(ItemValues itemValues,
                     List<String> photoPath) {

        String username = UserService.getInstance().getCurrentUser().getUsername();
        itemValues.addStringItem(MODULE, "primeromodule-cp");
        itemValues.addStringItem(CASEWORKER_CODE, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        Gson gson = new Gson();
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob tracingBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Tracing tracing = new Tracing();
        tracing.setUniqueId(createUniqueId());
        tracing.setCreateDate(date);
        tracing.setLastUpdatedDate(date);
        tracing.setContent(tracingBlob);
        tracing.setName(getName(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        tracing.setAge(age);
        tracing.setCaregiver(getCaregiverName(itemValues));
        tracing.setRegistrationDate(getRegisterDate(itemValues));
        tracing.setAudio(audioFileDefault);
        tracing.setCreatedBy(username);
        tracing.save();

        try {
            savePhoto(tracing, photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearAudioFile();
    }

    public void savePhoto(Tracing parent, List<String> photoPaths) throws IOException {
        for (int i = 0; i < photoPaths.size(); i++) {
            generateSavePhoto(parent, photoPaths, i).save();
        }
    }

    public void update(ItemValues itemValues,
                       List<String> photoBitPaths) {
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
        tracing.setRegistrationDate(getRegisterDate(itemValues));
        tracing.setAudio(audioFileDefault);
        tracing.update();

        try {
            updatePhoto(tracing, photoBitPaths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearAudioFile();
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
                TracingPhoto.setThumbnail(null);
                TracingPhoto.update();
            }
        }
    }

    private TracingPhoto generateSavePhoto(Tracing parent, List<String> photoPaths, int index) throws IOException {

        TracingPhoto TracingPhoto
                = tracingPhotoDao.getByTracingIdAndOrder(parent.getId(), index + 1);
        if (TracingPhoto == null) {
            TracingPhoto = new TracingPhoto();
        }
        String filePath = photoPaths.get(index);
        Bitmap bitmap = preProcessImage(filePath);
        TracingPhoto.setThumbnail(new Blob(ImageCompressUtil.convertImageToBytes(
                ImageCompressUtil.getThumbnail(bitmap, PhotoConfig.THUMBNAIL_SIZE,
                        PhotoConfig.THUMBNAIL_SIZE))));

        TracingPhoto.setPhoto(new Blob(ImageCompressUtil.convertImageToBytes(bitmap)));
        TracingPhoto.setTracing(parent);
        TracingPhoto.setOrder(index + 1);
        return TracingPhoto;
    }

    @NonNull
    private TracingPhoto generateUpdatePhoto(Tracing tracing, List<String> photoPaths, int index) throws IOException {
        TracingPhoto tracingPhoto;
        String filePath = photoPaths.get(index);
        Blob photo;
        try {
            long photoId = Long.parseLong(filePath);
            tracingPhoto = tracingPhotoDao.getById(photoId);
        } catch (NumberFormatException e) {
            Bitmap bitmap = preProcessImage(filePath);
            photo = new Blob(ImageCompressUtil.convertImageToBytes(bitmap));
            tracingPhoto = new TracingPhoto();
            tracingPhoto.setThumbnail(new Blob(ImageCompressUtil.convertImageToBytes(
                    ImageCompressUtil.getThumbnail(bitmap, PhotoConfig.THUMBNAIL_SIZE,
                            PhotoConfig.THUMBNAIL_SIZE))));
            tracingPhoto.setTracing(tracing);
            tracingPhoto.setPhoto(photo);
        }
        tracingPhoto.setId(tracingPhotoDao
                .getByTracingIdAndOrder(tracing.getId(), index + 1).getId());
        tracingPhoto.setOrder(index + 1);
        return tracingPhoto;
    }

    private Bitmap preProcessImage(String filePath) throws IOException {
        if (new File(filePath).length() <= 1024 * 1024 * 1) {
            return BitmapFactory.decodeFile(filePath);
        }
        return ImageCompressUtil.compressImage(filePath,
                PhotoConfig.MAX_WIDTH, PhotoConfig.MAX_HEIGHT);
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
}
