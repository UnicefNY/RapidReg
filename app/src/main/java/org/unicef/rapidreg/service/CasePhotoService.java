package org.unicef.rapidreg.service;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.model.CasePhoto;

import java.util.List;

public class CasePhotoService {
    public static final String TAG = CasePhotoService.class.getSimpleName();

    private static final CasePhotoService CASE_SERVICE = new CasePhotoService(new CasePhotoDaoImpl());

    private CasePhotoDao casePhotoDao;

    public static CasePhotoService getInstance() {
        return CASE_SERVICE;
    }

    public CasePhotoService(CasePhotoDao caseDao) {
        this.casePhotoDao = caseDao;
    }

    public CasePhoto getCaseFirstThumbnail(long caseId) {
        return casePhotoDao.getCaseFirstThumbnail(caseId);
    }

    public CasePhoto getCasePhotoById(long caseId) {
        return casePhotoDao.getCasePhotoById(caseId);
    }

    public List<CasePhoto> getAllCasesPhotoFlowQueryList(long caseId) {
        return casePhotoDao.getAllCasesPhotoFlowQueryList(caseId);
    }

    private long caseId;

    public void setCaseId(long caseId) {
        this.caseId = caseId;
    }

    public long getCaseId() {
        return caseId;
    }
}
