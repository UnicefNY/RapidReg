package org.unicef.rapidreg.service;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;

public class CaseService {
    public static final String TAG = CaseService.class.getSimpleName();

    private static final CaseService CASE_SERVICE = new CaseService(new CaseDaoImpl());
    private CaseDao caseDao;

    public static CaseService getInstance() {
        return CASE_SERVICE;
    }

    public CaseService(CaseDao caseDao) {
        this.caseDao = caseDao;
    }
}
