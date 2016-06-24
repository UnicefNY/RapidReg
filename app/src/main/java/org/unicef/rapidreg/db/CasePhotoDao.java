package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.CasePhoto;

import java.util.List;

public interface CasePhotoDao {
    List<CasePhoto> getAllCasesPhoto(long caseId);

    void deleteCasePhotosByCaseId(long caseId);
}
