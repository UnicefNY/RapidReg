package org.unicef.rapidreg.db.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.CasePhoto_Table;

import java.util.List;

public class CasePhotoDaoImpl implements CasePhotoDao {
    @Override
    public List<CasePhoto> getAllCasesPhoto(long caseId) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId))
                .queryList();
    }

    @Override
    public CasePhoto getCaseFirstThumbnail(long caseId) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId))
                .groupBy(CasePhoto_Table.thumbnail)
                .querySingle();
    }


    public void deleteCasePhotosByCaseId(long caseId) {
        SQLite.delete().from(CasePhoto.class).where(CasePhoto_Table.case_id.eq(caseId)).execute();
    }

    @Override
    public void getAllCasesPhoto(long caseId, QueryTransaction.QueryResultCallback<CasePhoto> callback) {
        SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId)).async().queryResultCallback(callback).execute();
    }
}
