package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.list.FlowCursorList;

import org.unicef.rapidreg.model.CasePhoto;

import java.util.List;

public interface CasePhotoDao {
    CasePhoto getCaseFirstThumbnail(long caseId);

    void deleteCasePhotosByCaseId(long caseId);

    List<CasePhoto> getAllCasesPhotoFlowQueryList(long caseId);

    CasePhoto getCasePhotoById(long id);

    void deletePhotoById(long id);
}
