package org.unicef.rapidreg.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.model.Case;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CaseService {
    public static final String TAG = CaseService.class.getSimpleName();

    private static final CaseService CASE_SERVICE = new CaseService(new CaseDaoImpl());
    private static final String UNIQUE_ID = "unique_id";
    private CaseDao caseDao;

    public static CaseService getInstance() {
        return CASE_SERVICE;
    }

    public CaseService(CaseDao caseDao) {
        this.caseDao = caseDao;
    }

    public List<Case> getCaseList() {
        return caseDao.getAllCases();
    }
    public List<Case> getCaseListOrderByAge() {
        return caseDao.getAllCasesOrderByAge();
    }

    public Map<String, String> getCaseMapByUniqueId(String uniqueId) {
        Case child = caseDao.getCaseByUniqueId(uniqueId);
        if (child == null) {
            return new HashMap<>();
        }

        String caseJson = new String(child.getContent().getBlob());
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> values = new Gson().fromJson(caseJson, type);

        values.put(UNIQUE_ID, uniqueId);

        return values;
    }

    public void saveOrUpdateCase(Map<String, String> values) {
        Date date = new Date(Calendar.getInstance().getTimeInMillis());

        String caseJson = new Gson().toJson(values);
        Blob caseBlob = new Blob(caseJson.getBytes());

        String uniqueId = values.get(UNIQUE_ID);
        if (uniqueId == null) {
            Log.d(TAG, "save a new case");
            Case child = new Case();
            child.setUniqueId(createUniqueId());
            child.setCreateAt(date);
            child.setLastUpdatedAt(date);
            child.setContent(caseBlob);
            child.setAge(Integer.parseInt(values.get("Age")));
            child.save();
        } else {
            Log.d(TAG, "update the existing case");
            Case child = caseDao.getCaseByUniqueId(uniqueId);
            child.setLastUpdatedAt(date);
            child.setContent(caseBlob);
            child.setAge(Integer.parseInt(values.get("Age")));
            child.update();
        }
    }

    public String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static class CaseValues {
        private static Map<String, String> values = new HashMap<>();

        public static Map<String, String> getValues() {
            return values;
        }

        public static void setValues(Map<String, String> valuesMap) {
            values.putAll(valuesMap);
        }

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
