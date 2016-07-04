package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.list.FlowCursorList;

import org.unicef.rapidreg.model.CasePhoto;

public interface CasePhotoDao {

    CasePhoto getCaseFirstThumbnail(long caseId);

    void deleteCasePhotosByCaseId(long caseId);

    FlowCursorList<CasePhoto> getAllCasesPhotoFlowQueryList(long caseId);
}
