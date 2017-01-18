package org.unicef.rapidreg.repository;

import org.unicef.rapidreg.model.CasePhoto;

import java.util.List;

public interface CasePhotoDao {
    CasePhoto getFirst(long caseId);

    List<Long> getIdsByCaseId(long caseId);

    CasePhoto getByCaseIdAndOrder(long caseId, int order);

    CasePhoto getById(long id);

    long countUnSynced(long caseId);

    void deleteByCaseId(long caseId);

    CasePhoto save(CasePhoto casePhoto);
}
