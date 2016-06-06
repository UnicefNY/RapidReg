package org.unicef.rapidreg.service;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.form.childcase.CaseFormRoot;

public class CaseFormService {
    public static final String TAG = CaseFormService.class.getSimpleName();
    private static final CaseFormService CASE_FORM_SERVICE
            = new CaseFormService(new CaseFormDaoImpl());
    private CaseFormDao caseFormDao;

    public static CaseFormService getInstance() {
        return CASE_FORM_SERVICE;
    }

    public CaseFormService(CaseFormDao caseFormDao) {
        this.caseFormDao = caseFormDao;
    }

    public CaseFormRoot getCurrentForm() {
        Blob form = caseFormDao.getForm();
        String formJson = new String(form.getBlob());
        return TextUtils.isEmpty(formJson) ?
                null : new Gson().fromJson(formJson, CaseFormRoot.class);
    }

    public void saveOrUpdateCaseForm(CaseForm caseForm) {
        CaseForm existingCaseForm = caseFormDao.getCaseForm();

        if (existingCaseForm == null) {
            Log.d(TAG, "save new case form");
            caseForm.save();
        } else {
            Log.d(TAG, "update existing case form");
            existingCaseForm.setForm(caseForm.getForm());
            existingCaseForm.update();
        }
    }
}
