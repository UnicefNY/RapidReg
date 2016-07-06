package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.CasePhoto_Table;

import java.util.List;

public class CasePhotoDaoImpl implements CasePhotoDao {
    @Override
    public CasePhoto getCaseFirstThumbnail(long caseId) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId))
                .groupBy(CasePhoto_Table.thumbnail)
                .querySingle();
    }

    @Override
    public void deleteCasePhotosByCaseId(long caseId) {
        SQLite.delete().from(CasePhoto.class).where(CasePhoto_Table.case_id.eq(caseId)).execute();
    }

    @Override
    public List<CasePhoto> getAllCasesPhotoFlowQueryList(long caseId) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId)).and(CasePhoto_Table.photo.isNotNull()).queryList();
    }

    @Override
    public CasePhoto getCasePhotoById(long id) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.id.eq(id)).querySingle();
    }

    @Override
    public void deletePhotoById(long id) {
        SQLite.delete().from(CasePhoto.class).where(CasePhoto_Table.id.eq(id)).execute();
    }
}
