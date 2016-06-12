package org.unicef.rapidreg.service;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;

import java.util.HashMap;
import java.util.Map;

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

    public static class CaseValues {
        private static Map<String, String> values = new HashMap<>();

        public static void put(String key, String value) {
            values.put(key, value);
        }

        public static String get(String key) {
            return values.get(key);
        }

        public static void clear() {
            values.clear();
        }
    }
}
