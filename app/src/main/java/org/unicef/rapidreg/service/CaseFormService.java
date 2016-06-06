package org.unicef.rapidreg.service;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.model.form.childcase.CaseForm;

public class CaseFormService {
    private static final CaseFormService CASE_FORM_SERVICE
            = new CaseFormService(new CaseFormDaoImpl());
    private CaseFormDao caseFormDao;

    public static CaseFormService getInstance() {
        return CASE_FORM_SERVICE;
    }

    public CaseFormService(CaseFormDao caseFormDao) {
        this.caseFormDao = caseFormDao;
    }

    public CaseForm getCurrentForm() {
        Blob form = caseFormDao.getForm();
        String formJson = new String(form.getBlob());
        return TextUtils.isEmpty(formJson) ?
                null : new Gson().fromJson(formJson, CaseForm.class);
    }
}
