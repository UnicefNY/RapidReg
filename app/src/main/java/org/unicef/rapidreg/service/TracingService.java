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
            return tracingPhotoDao.save(tracingDao.save(itemValues), photoPaths);
        } else {
            Log.d(TAG, "update the existing tracing request");
            return tracingPhotoDao.update(tracingDao.update(itemValues), photoPaths);
        }
    }

    public Tracing save(ItemValues itemValues, List<String> photoPaths) throws IOException {
        return tracingPhotoDao.save(tracingDao.save(itemValues), photoPaths);
    }

    public Tracing update(ItemValues itemValues,
                          List<String> photoBitPaths) throws IOException {
        return tracingPhotoDao.update(tracingDao.update(itemValues), photoBitPaths);
    }

    public void savePhoto(Tracing parent, List<String> photoPaths) throws IOException {
        tracingPhotoDao.save(parent, photoPaths);
    }

    public Tracing getByInternalId(String id) {
        return tracingDao.getByInternalId(id);
    }

    public boolean hasSameRev(String id, String rev) {
        Tracing tracing = tracingDao.getByInternalId(id);
        return tracing != null && rev.equals(tracing.getInternalRev());
    }
}
