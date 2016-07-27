package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.CasePhoto;

import java.util.List;

public interface CasePhotoDao {
    CasePhoto getFirst(long caseId);

    List<CasePhoto> getByCaseId(long caseId);

    CasePhoto getByCaseIdAndOrder(long caseId, int order);

    CasePhoto getById(long id);

    long countUnSynced(long caseId);

    void deleteByCaseId(long caseId);
}
