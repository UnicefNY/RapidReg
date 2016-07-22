package org.unicef.rapidreg.service;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.model.CasePhoto;

import java.util.List;

public class CasePhotoService {
    public static final String TAG = CasePhotoService.class.getSimpleName();

    private static final CasePhotoService CASE_SERVICE
            = new CasePhotoService(new CasePhotoDaoImpl());

    private CasePhotoDao casePhotoDao;

    public static CasePhotoService getInstance() {
        return CASE_SERVICE;
    }

    public CasePhotoService(CasePhotoDao caseDao) {
        this.casePhotoDao = caseDao;
    }

    public CasePhoto getFirstThumbnail(long caseId) {
        return casePhotoDao.getFirstThumbnail(caseId);
    }

    public CasePhoto getById(long caseId) {
        return casePhotoDao.getById(caseId);
    }

    public List<CasePhoto> getByCaseId(long caseId) {
        return casePhotoDao.getByCaseId(caseId);
    }

    public boolean hasUnSynced(long caseId) {
        return casePhotoDao.countUnSynced(caseId) > 0;
    }
}
