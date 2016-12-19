package org.unicef.rapidreg.service;

import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.db.TracingDao;
import org.unicef.rapidreg.db.TracingPhotoDao;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.StreamUtil;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.unicef.rapidreg.db.impl.TracingDaoImpl.TRACING_DISPLAY_ID;
import static org.unicef.rapidreg.db.impl.TracingDaoImpl.TRACING_ID;
import static org.unicef.rapidreg.service.CaseService.CASE_ID;
import static org.unicef.rapidreg.utils.Utils.getRegisterDate;

public class TracingService extends RecordService {
    public static final String TAG = TracingService.class.getSimpleName();

    private final TracingDao tracingDao;
    private final TracingPhotoDao tracingPhotoDao;

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
        return tracingDao.getAllTracingsOrderByDate(false);
    }

    public List<Long> getAllIds() {
        return tracingDao.getAllIds();
    }

    public List<Long> getAllOrderByDateASC() {
        return extractIds(tracingDao.getAllTracingsOrderByDate(true));
    }

    public List<Long> getAllOrderByDateDES() {
        return extractIds(tracingDao.getAllTracingsOrderByDate(false));
    }

    public List<Long> getSearchResult(String uniqueId, String name, int ageFrom, int ageTo, Date date) {
        ConditionGroup searchCondition = getSearchCondition(uniqueId, name, ageFrom, ageTo, date);
        return extractIds(tracingDao.getAllTracingsByConditionGroup(searchCondition));
    }

    private List<Long> extractIds(List<Tracing> tracings) {
        List<Long> result = new ArrayList<>();
        for (Tracing tracing : tracings) {
            result.add(tracing.getId());
        }
        return result;
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

    public Tracing save(ItemValues itemValues, List<String> photoPaths) throws IOException {
        Tracing tracing = tracingDao.save(generateTracingFromItemValues(itemValues, generateUniqueId()));

        return tracingPhotoDao.save(tracing, photoPaths);
    }

    public Tracing update(ItemValues itemValues,
                          List<String> photoBitPaths) throws IOException {
        Tracing tracing = tracingDao.update(generateTracingFromItemValues(itemValues));
        return tracingPhotoDao.update(tracing, photoBitPaths);
    }

    private Tracing generateTracingFromItemValues(ItemValues itemValues, String uniqueId) {
        Tracing tracing = new Tracing();
        tracing.setUniqueId(uniqueId);

        String username = PrimeroConfiguration.getCurrentUser().getUsername();
        tracing.setCreatedBy(username);

        tracing.setContent(generateTracingBlob(itemValues, uniqueId, username));

        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        tracing.setCreateDate(date);
        tracing.setLastUpdatedDate(date);

        tracing.setName(getName(itemValues));

        int age = itemValues.getAsInt(RELATION_AGE) != null ? itemValues.getAsInt(RELATION_AGE) : 0;
        tracing.setAge(age);

        tracing.setCaregiver(getCaregiverName(itemValues));
        tracing.setRegistrationDate(getRegisterDate(itemValues.getAsString(INQUIRY_DATE)));
        tracing.setAudio(getAudioBlob());

        return tracing;
    }

    private Tracing generateTracingFromItemValues(ItemValues itemValues) {
        Tracing tracing = tracingDao.getTracingByUniqueId(itemValues.getAsString(TRACING_ID));
        String uniqueId = tracing.getUniqueId();

        return generateTracingFromItemValues(itemValues, uniqueId);
    }

    private Blob generateTracingBlob(ItemValues itemValues, String uniqueId, String username) {
        itemValues.addStringItem(TRACING_DISPLAY_ID, getShortUUID(uniqueId));
        itemValues.addStringItem(TRACING_ID, uniqueId);
        itemValues.addStringItem(MODULE, "primeromodule-cp");
        itemValues.addStringItem(CASEWORKER_CODE, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        if (!itemValues.has(INQUIRY_DATE)) {
            itemValues.addStringItem(INQUIRY_DATE, getCurrentRegistrationDateAsString());
        }

        return new Blob(new Gson().toJson(itemValues.getValues()).getBytes());
    }

    private String getName(ItemValues values) {
        return values.getAsString(RELATION_NAME) + " "
                + values.getAsString(RELATION_AGE) + " "
                + values.getAsString(RELATION_NICKNAME);
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
