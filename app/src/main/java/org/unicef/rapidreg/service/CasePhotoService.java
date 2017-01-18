package org.unicef.rapidreg.service;

import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.repository.impl.CasePhotoDaoImpl;
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

    public CasePhoto getFirst(long caseId) {
        return casePhotoDao.getFirst(caseId);
    }

    public CasePhoto getById(long id) {
        return casePhotoDao.getById(id);
    }

    public List<Long> getIdsByCaseId(long caseId) {
        return casePhotoDao.getIdsByCaseId(caseId);
    }

    public boolean hasUnSynced(long caseId) {
        return casePhotoDao.countUnSynced(caseId) > 0;
    }

    public void deleteByCaseId(long caseId) {
        casePhotoDao.deleteByCaseId(caseId);
    }
}
