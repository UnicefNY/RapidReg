package org.unicef.rapidreg.service;

import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.repository.TracingDao;
import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.repository.impl.TracingDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.StreamUtil;
import org.unicef.rapidreg.utils.TextUtils;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;
import static org.unicef.rapidreg.utils.Utils.getRegisterDate;

public class TracingService extends RecordService {
    public static final String TAG = TracingService.class.getSimpleName();
    public static final String TRACING_DISPLAY_ID = "tracing_request_id_display";
    public static final String TRACING_ID = "tracing_request_id";
    public static final String TRACING_PRIMARY_ID = "tracing_primary_id";

    private TracingDao tracingDao = new TracingDaoImpl();
    private TracingPhotoDao tracingPhotoDao = new TracingPhotoDaoImpl();

    public TracingService(TracingDao tracingDao, TracingPhotoDao tracingPhotoDao) {
        this.tracingDao = tracingDao;
        this.tracingPhotoDao = tracingPhotoDao;
    }

    public Tracing getById(long tracingId) {
        return tracingDao.getTracingById(tracingId);
    }

    public Tracing getByUniqueId(String uniqueId) {
        return tracingDao.getTracingByUniqueId(uniqueId);
    }

    public List<Tracing> getAll() {
        return tracingDao.getAllTracingsOrderByDate(false, PrimeroAppConfiguration.getCurrentUsername(),
                PrimeroAppConfiguration.getApiBaseUrl());
    }

    public List<Long> getAllOrderByDateASC() {
        return extractIds(tracingDao.getAllTracingsOrderByDate(true, PrimeroAppConfiguration.getCurrentUsername(),
                PrimeroAppConfiguration.getApiBaseUrl()));
    }

    public List<Long> getAllOrderByDateDES() {
        return extractIds(tracingDao.getAllTracingsOrderByDate(false, PrimeroAppConfiguration.getCurrentUsername(),
                PrimeroAppConfiguration.getApiBaseUrl()));
    }

    public List<Long> getSearchResult(String uniqueId, String name, int ageFrom, int ageTo, Date
            date) {
        ConditionGroup searchCondition = getSearchCondition(uniqueId, name, ageFrom, ageTo, date);
        return extractIds(tracingDao.getAllTracingsByConditionGroup(PrimeroAppConfiguration.getCurrentUsername(),
                PrimeroAppConfiguration.getApiBaseUrl(),
                searchCondition));
    }

    private ConditionGroup getSearchCondition(String uniqueId, String name, int ageFrom, int
            ageTo, Date date) {
        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_SHORT_ID).build())
                .like(getWrappedCondition(uniqueId)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_NAME).build())
                .like(getWrappedCondition(name)));

        SQLCondition ageSearchCondition = generateAgeSearchCondition(ageFrom, ageTo);
        if (ageSearchCondition != null) {
            conditionGroup.and(ageSearchCondition);
        }
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_CREATED_BY).build())
                .eq(PrimeroAppConfiguration.getCurrentUser().getUsername()));

        if (date != null) {
            conditionGroup.and(Condition.column(
                    NameAlias.builder(RecordModel.COLUMN_REGISTRATION_DATE).build()).eq(date));
        }
        return conditionGroup;
    }

    public Tracing saveOrUpdate(ItemValuesMap itemValues, List<String> photoPaths) throws
            IOException {
        if (itemValues.getAsString(TRACING_ID) == null) {
            return save(itemValues, photoPaths);
        } else {
            Log.d(TAG, "update the existing tracing request");
            return update(itemValues, photoPaths);
        }
    }

    public Tracing save(ItemValuesMap itemValues, List<String> photoPaths) throws IOException {
        Tracing tracing = generateTracingFromItemValues(itemValues,
                generateUniqueId());
        tracingDao.save(tracing);
        tracingPhotoDao.save(tracing, photoPaths);
        return tracing;
    }

    public Tracing update(ItemValuesMap itemValues,
                          List<String> photoBitPaths) throws IOException {
        Tracing tracing = updateTracingFromItemValues(itemValues);
        tracing.setSynced(false);
        return tracingPhotoDao.update(tracingDao.update(tracing), photoBitPaths);
    }

    private Tracing generateTracingFromItemValues(ItemValuesMap itemValues, String uniqueId) {
        if (!itemValues.has(INQUIRY_DATE)) {
            itemValues.addStringItem(INQUIRY_DATE, getCurrentRegistrationDateAsString());
        }

        Tracing tracing = new Tracing();
        tracing.setUniqueId(uniqueId);
        tracing.setShortId(getShortUUID(uniqueId));

        String username = PrimeroAppConfiguration.getCurrentUser().getUsername();
        tracing.setCreatedBy(username);
        tracing.setOwnedBy(username);
        tracing.setUrl(TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl()));

        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        tracing.setCreateDate(date);
        tracing.setLastUpdatedDate(date);

        tracing.setName(getName(itemValues));

        int age = itemValues.getAsInt(RELATION_AGE) != null ? itemValues.getAsInt(RELATION_AGE) : EMPTY_AGE;
        tracing.setAge(age);

        tracing.setCaregiver(getCaregiverName(itemValues));
        tracing.setRegistrationDate(getRegisterDate(itemValues.getAsString(INQUIRY_DATE)));
        tracing.setAudio(getAudioBlob());

        tracing.setContent(generateTracingBlob(itemValues, uniqueId, username));

        return tracing;
    }

    private Tracing updateTracingFromItemValues(ItemValuesMap itemValues) {
        Tracing tracing = tracingDao.getTracingByUniqueId(itemValues.getAsString(TRACING_ID));
        String username = PrimeroAppConfiguration.getCurrentUser().getUsername();

        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        tracing.setCreateDate(date);
        tracing.setLastUpdatedDate(date);
        tracing.setName(getName(itemValues));

        int age = itemValues.getAsInt(RELATION_AGE) != null ? itemValues.getAsInt(RELATION_AGE) : 0;
        tracing.setAge(age);

        tracing.setCaregiver(getCaregiverName(itemValues));
        tracing.setRegistrationDate(getRegisterDate(itemValues.getAsString(INQUIRY_DATE)));
        tracing.setAudio(getAudioBlob());

        tracing.setContent(generateTracingBlob(itemValues, tracing.getUniqueId(), username));

        return tracing;
    }

    private Blob generateTracingBlob(ItemValuesMap itemValues, String uniqueId, String username) {
        itemValues.addStringItem(TRACING_DISPLAY_ID, getShortUUID(uniqueId));
        itemValues.addStringItem(TRACING_ID, uniqueId);
        itemValues.addStringItem(MODULE, MODULE_CP_CASE);
        itemValues.addStringItem(RECORD_OWNED_BY, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        if (!itemValues.has(INQUIRY_DATE)) {
            itemValues.addStringItem(INQUIRY_DATE, getCurrentRegistrationDateAsString());
        }

        return new Blob(new Gson().toJson(itemValues.getValues()).getBytes());
    }

    private String getName(ItemValuesMap values) {
        return values.concatMultiStringsWithBlank(RELATION_NAME, RELATION_AGE, RELATION_NICKNAME);
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

    public Tracing getByInternalId(String id) {
        return tracingDao.getByInternalId(id);
    }

    public boolean hasSameRev(String id, String rev) {
        Tracing tracing = tracingDao.getByInternalId(id);
        return tracing != null && rev.equals(tracing.getInternalRev());
    }
}
