package org.unicef.rapidreg.service;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.data.Blob;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public List<CasePhoto> getAllCasePhotos(long caseId){
        return casePhotoDao.getAllCasesPhoto(caseId);
    }
}
