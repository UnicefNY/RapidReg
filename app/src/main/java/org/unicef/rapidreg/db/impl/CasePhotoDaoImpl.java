package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.CasePhoto_Table;

import java.util.ArrayList;
import java.util.List;

public class CasePhotoDaoImpl implements CasePhotoDao {
    @Override
    public CasePhoto getFirst(long caseId) {
        return SQLite.select().from(CasePhoto.class)
                .where(CasePhoto_Table.caseId.eq(caseId))
                .querySingle();
    }

    @Override
    public List<Long> getIdsByCaseId(long caseId) {
        CasePhoto_Table.index_indexCaseId.createIfNotExists();

        List<Long> result = new ArrayList<>();
        List<CasePhoto> casePhotos = SQLite.select(CasePhoto_Table.id)
                .from(CasePhoto.class)
                .indexedBy(CasePhoto_Table.index_indexCaseId)
                .where(CasePhoto_Table.caseId.eq(caseId))
                .and(CasePhoto_Table.photo.isNotNull())
                .queryList();
        for (CasePhoto casePhoto : casePhotos) {
            result.add(casePhoto.getId());
        }
        return result;
    }

    @Override
    public CasePhoto getById(long id) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public long countUnSynced(long caseId) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.caseId.eq(caseId))
                .and(CasePhoto_Table.photo.isNotNull())
                .and(CasePhoto_Table.isSynced.is(false))
                .count();
    }

    @Override
    public void deleteByCaseId(long caseId) {
        SQLite.delete().from(CasePhoto.class).where(CasePhoto_Table.caseId.eq(caseId)).execute();
    }

    @Override
    public CasePhoto getByCaseIdAndOrder(long caseId, int order) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.caseId.eq(caseId))
                .and(CasePhoto_Table.order.eq(order))
                .querySingle();
    }
}
