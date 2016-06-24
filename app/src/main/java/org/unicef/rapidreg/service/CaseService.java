package org.unicef.rapidreg.service;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.utils.ImageCompressUtil;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static final CaseService CASE_SERVICE = new CaseService();

    private CaseDao caseDao = new CaseDaoImpl();
    private CasePhotoDao casePhotoDao = new CasePhotoDaoImpl();

    public static CaseService getInstance() {
        return CASE_SERVICE;
    }

    private CaseService() {
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

    public List<Case> getSearchResult(String uniqueId, String name, int ageFrom, int ageTo,
                                      String caregiver, String date) {

        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder("unique_id").build())
                .like(getWrappedCondition(uniqueId)));
        conditionGroup.and(Condition.column(NameAlias.builder("name").build())
                .like(getWrappedCondition(name)));
        conditionGroup.and(Condition.column(NameAlias.builder("age").build())
                .between(ageFrom).and(ageTo));

        return caseDao.getCaseListByConditionGroup(conditionGroup);
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

    public void saveOrUpdateCase(Map<String, String> values, Map<Bitmap, String> photoBitPaths) {
        if (values.get(UNIQUE_ID) == null) {
            saveCase(values, photoBitPaths);
        } else {
            Log.d(TAG, "update the existing case");
            updateCase(values, photoBitPaths);
        }
    }

    private void saveCase(Map<String, String> values, Map<Bitmap, String> photoBitPaths) {
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob caseBlob = new Blob(new Gson().toJson(values).getBytes());

        Case child = new Case();
        child.setUniqueId(createUniqueId());
        child.setCreateAt(date);
        child.setLastUpdatedAt(date);
        child.setContent(caseBlob);
        child.setAge(Integer.parseInt(values.get("Age")));
        child.save();

        saveCasePhoto(child, photoBitPaths);
    }

    private void updateCase(Map<String, String> values, Map<Bitmap, String> photoBitPaths) {
        Blob caseBlob = new Blob(new Gson().toJson(values).getBytes());

        Case child = caseDao.getCaseByUniqueId(values.get(UNIQUE_ID));
        child.setLastUpdatedAt(new Date(Calendar.getInstance().getTimeInMillis()));
        child.setContent(caseBlob);
        child.setAge(Integer.parseInt(values.get("Age")));
        child.update();

        casePhotoDao.deleteCasePhotosByCaseId(child.getId());
        saveCasePhoto(child, photoBitPaths);
    }

    private void saveCasePhoto(Case child, Map<Bitmap, String> photoBitPaths) {
        if (photoBitPaths != null) {
            for (Map.Entry<Bitmap, String> photoBitPathEntry : photoBitPaths.entrySet()) {
                CasePhoto casePhoto = new CasePhoto();
                casePhoto.setPath(photoBitPathEntry.getValue());
                casePhoto.setPhoto(new Blob(ImageCompressUtil.convertImageToBytes(photoBitPathEntry.getValue())));
                casePhoto.setThumbnail(new Blob(ImageCompressUtil.convertImageToBytes(photoBitPathEntry.getKey())));
                casePhoto.setCase(child);
                casePhoto.save();
                Log.i("sjyuan", "saved casePhoto = " + casePhoto.toString());
            }
        }
    }

    private Date getCurrentDate() {
        return new Date(Calendar.getInstance().getTimeInMillis());
    }

    private Date getRegisterDate(Map<String, String> values) {
        String key = "Date of Registration or Interview";
        if (values.containsKey(key)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                java.util.Date date = simpleDateFormat.parse(values.get(key));
                return new Date(date.getTime());
            } catch (ParseException e) {
                Log.e(TAG, "date format error");
            }
        }

        return getCurrentDate();
    }

    private String getWrappedCondition(String queryStr) {
        return "%" + queryStr + "%";
    }

    public String createUniqueId() {
        return UUID.randomUUID().toString();
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

        public static void addPhoto(ContentResolver contentResolver, String photoPath) {
            addPhoto(ImageCompressUtil.getThumbnail(contentResolver, photoPath), photoPath);
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
