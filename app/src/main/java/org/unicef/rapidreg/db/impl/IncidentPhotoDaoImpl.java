package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.IncidentPhotoDao;
import org.unicef.rapidreg.model.IncidentPhoto;
import org.unicef.rapidreg.model.IncidentPhoto_Table;

import java.util.ArrayList;
import java.util.List;

public class IncidentPhotoDaoImpl implements IncidentPhotoDao {
    @Override
    public IncidentPhoto getFirst(long incidentId) {
        return SQLite.select().from(IncidentPhoto.class)
                .where(IncidentPhoto_Table.incidentId.eq(incidentId))
                .querySingle();    }

    @Override
    public List<Long> getIdsByIncidentId(long incidentId) {
        IncidentPhoto_Table.index_indexIncidentId.createIfNotExists();

        List<Long> result = new ArrayList<>();
        List<IncidentPhoto> incidentPhotos = SQLite.select(IncidentPhoto_Table.id)
                .from(IncidentPhoto.class)
                .indexedBy(IncidentPhoto_Table.index_indexIncidentId)
                .where(IncidentPhoto_Table.incidentId.eq(incidentId))
                .and(IncidentPhoto_Table.photo.isNotNull())
                .queryList();
        for (IncidentPhoto incidentPhoto : incidentPhotos) {
            result.add(incidentPhoto.getId());
        }
        return result;
    }

    @Override
    public IncidentPhoto getById(long id) {
        return SQLite.select()
                .from(IncidentPhoto.class)
                .where(IncidentPhoto_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public long countUnSynced(long incidentId) {
        return SQLite.select()
                .from(IncidentPhoto.class)
                .where(IncidentPhoto_Table.incidentId.eq(incidentId))
                .and(IncidentPhoto_Table.photo.isNotNull())
                .and(IncidentPhoto_Table.isSynced.is(false))
                .count();
    }

    @Override
    public IncidentPhoto getByIncidentIdAndOrder(long incidentId, int order) {
        return SQLite.select()
                .from(IncidentPhoto.class)
                .where(IncidentPhoto_Table.incidentId.eq(incidentId))
                .and(IncidentPhoto_Table.order.eq(order))
                .querySingle();
    }

    @Override
    public void deleteByIncidentId(long incidentId) {
        SQLite.delete().from(IncidentPhoto.class).where(IncidentPhoto_Table.
                incidentId.eq(incidentId)).execute();
    }
}
