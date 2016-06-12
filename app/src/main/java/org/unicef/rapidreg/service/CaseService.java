package org.unicef.rapidreg.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.model.Case;

import java.lang.reflect.Type;
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

    public Map<String, String> getCaseMapById(long id) {
        Case child = caseDao.getCaseById(id);
        String caseJson = new String(child.getContent().getBlob());
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();

        return new Gson().fromJson(caseJson, type);
    }

    public void saveOrUpdateCase(long id) {
        String caseJson = new Gson().toJson(CaseValues.values);
        Blob caseBlob = new Blob(caseJson.getBytes());

        Case child = caseDao.getCaseById(id);
        if (child == null) {
            Log.d(TAG, "save a new case");
            child = new Case();
            child.setContent(caseBlob);
            child.save();
        } else {
            Log.d(TAG, "update the existing case");
            child.setContent(caseBlob);
            child.update();
        }
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
