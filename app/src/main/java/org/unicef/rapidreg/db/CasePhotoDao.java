package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import org.unicef.rapidreg.model.CasePhoto;

import java.util.List;

public interface CasePhotoDao {
    List<CasePhoto> getAllCasesPhoto(long caseId);

    CasePhoto getCaseFirstThumbnail(long caseId);

    void deleteCasePhotosByCaseId(long caseId);

    void getAllCasesPhoto(long caseId, QueryTransaction.QueryResultCallback<CasePhoto> callback);
}
