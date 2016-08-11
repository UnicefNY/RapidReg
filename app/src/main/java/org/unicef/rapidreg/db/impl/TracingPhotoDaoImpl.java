package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.TracingPhotoDao;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.model.TracingPhoto_Table;

import java.util.ArrayList;
import java.util.List;

public class TracingPhotoDaoImpl implements TracingPhotoDao {
    @Override
    public TracingPhoto getFirst(long tracingId) {
        return SQLite.select().from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracingId.eq(tracingId))
                .querySingle();
    }

    @Override
    public List<Long> getIdsByTracingId(long tracingId) {
        TracingPhoto_Table.index_indexTracingId.createIfNotExists();

        List<Long> result = new ArrayList<>();
        List<TracingPhoto> tracingPhotos = SQLite.select(TracingPhoto_Table.id)
                .from(TracingPhoto.class)
                .indexedBy(TracingPhoto_Table.index_indexTracingId)
                .where(TracingPhoto_Table.tracingId.eq(tracingId))
                .and(TracingPhoto_Table.photo.isNotNull())
                .queryList();
        for (TracingPhoto tracingPhoto : tracingPhotos) {
            result.add(tracingPhoto.getId());
        }
        return result;
    }

    @Override
    public TracingPhoto getById(long id) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public long countUnSynced(long tracingId) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracingId.eq(tracingId))
                .and(TracingPhoto_Table.photo.isNotNull())
                .and(TracingPhoto_Table.isSynced.is(false))
                .count();
    }

    @Override
    public void deleteByTracingId(long tracingId) {
        SQLite.delete().from(TracingPhoto.class).where(TracingPhoto_Table.tracingId.eq(tracingId)).execute();
    }

    @Override
    public TracingPhoto getByTracingIdAndOrder(long tracingId, int order) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracingId.eq(tracingId))
                .and(TracingPhoto_Table.order.eq(order))
                .querySingle();
    }
}
