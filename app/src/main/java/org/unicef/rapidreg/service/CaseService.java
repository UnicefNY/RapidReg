package org.unicef.rapidreg.service;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CaseService {
    public static final String TAG = CaseService.class.getSimpleName();
    public static final String UNIQUE_ID = "unique_id";

    private static final CaseService CASE_SERVICE = new CaseService(new CaseDaoImpl());


    private CaseDao caseDao;

    public static CaseService getInstance() {
        return CASE_SERVICE;
    }

    public CaseService(CaseDao caseDao) {
        this.caseDao = caseDao;
    }

    public List<Case> getCaseList() {
        return caseDao.getAllCasesOrderByDate(false);
    }

    public List<Case> getCaseListOrderByDateASC() {
        return caseDao.getAllCasesOrderByDate(true);
    }

    public List<Case> getCaseListOrderByDateDES() {
        return caseDao.getAllCasesOrderByDate(false);
    }

    public List<Case> getCaseListOrderByAgeASC() {
        return caseDao.getAllCasesOrderByAge(true);
    }

    public List<Case> getCaseListOrderByAgeDES() {
        return caseDao.getAllCasesOrderByAge(false);
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

    public List<String> fetchRequiredFiledNames(List<CaseField> caseFields) {
        List<String> result = new ArrayList<>();
        for (CaseField field : caseFields) {
            if (field.isRequired()) {
                result.add(field.getDisplayName().get("en"));
            }
        }
        return result;
    }

    public static class CaseValues {
        private static Map<String, String> values = new HashMap<>();
        private static Map<Bitmap, String> photoBitPaths = new LinkedHashMap<>();

        public static Map<String, String> getValues() {
            return values;
        }

        public static void setValues(Map<String, String> valuesMap) {
            values.putAll(valuesMap);
        }

        public static void setValues(Map<String, String> valuesMap, Map<Bitmap, String> photoPaths) {
            setValues(valuesMap);
            if (null != photoPaths) {
                for (Map.Entry<Bitmap, String> photo : photoPaths.entrySet()) {
                    photoBitPaths.put(photo.getKey(), photo.getValue());
                }

            }
        }

        public static void put(String key, String value) {
            values.put(key, value);
        }

        public static String get(String key) {
            return values.get(key);
        }

        public static void clear() {
            values.clear();
            photoBitPaths.clear();
        }


        public static void addPhoto(Bitmap bitmap, String photoPath) {
            photoBitPaths.put(bitmap, photoPath);
        }

        public static void removePhoto(Bitmap bitmap) {
            photoBitPaths.remove(bitmap);
        }

        public static Map<Bitmap, String> getPhotoBitPaths() {
            return photoBitPaths;
        }

        public static List<Bitmap> getPhotosBits() {
            List<Bitmap> result = new ArrayList<>();
            for (Bitmap photoBit : photoBitPaths.keySet()) {
                result.add(photoBit);
            }
            return result;
        }

        public static List<String> getPhotosPaths() {
            List<String> result = new ArrayList<>();
            for (String photoPath : photoBitPaths.values()) {
                result.add(photoPath);
            }
            return result;
        }
    }
}
