package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.CasePhoto_Table;

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
    public FlowCursorList<CasePhoto> getAllCasesPhotoFlowQueryList(long caseId) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId)).cursorList();
    }
}
