package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.CasePhoto;

import java.util.List;

public interface CasePhotoDao {
    CasePhoto getFirstThumbnail(long caseId);

    List<CasePhoto> getByCaseId(long caseId);

    CasePhoto getByCaseIdAndOrder(long caseId, int order);

    CasePhoto getById(long id);
}
