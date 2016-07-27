package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.CasePhoto_Table;

import java.util.List;

public class CasePhotoDaoImpl implements CasePhotoDao {
    @Override
    public CasePhoto getFirst(long caseId) {
        return SQLite.select().from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId))
                .querySingle();
    }

    @Override
    public List<CasePhoto> getByCaseId(long caseId) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId))
                .and(CasePhoto_Table.photo.isNotNull())
                .queryList();
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
                .where(CasePhoto_Table.case_id.eq(caseId))
                .and(CasePhoto_Table.photo.isNotNull())
                .and(CasePhoto_Table.isSynced.is(false))
                .count();
    }

    @Override
    public void deleteByCaseId(long caseId) {
        SQLite.delete().from(CasePhoto.class).where(CasePhoto_Table.case_id.eq(caseId)).execute();
    }

    @Override
    public CasePhoto getByCaseIdAndOrder(long caseId, int order) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId))
                .and(CasePhoto_Table.order.eq(order))
                .querySingle();
    }
}
