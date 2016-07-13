package org.unicef.rapidreg.service;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.model.CaseForm;

public class CaseFormService {
    public static final String TAG = CaseFormService.class.getSimpleName();
    private static final CaseFormService CASE_FORM_SERVICE
            = new CaseFormService(new CaseFormDaoImpl());
    private static CaseFormRoot caseForm;
    private CaseFormDao caseFormDao;

    public static CaseFormService getInstance() {
        return CASE_FORM_SERVICE;
    }

    public CaseFormService(CaseFormDao caseFormDao) {
        this.caseFormDao = caseFormDao;
    }

    public boolean isFormReady() {
        Blob form = caseFormDao.getCaseFormContent();

        return form != null;
    }

    public CaseFormRoot getCurrentForm() {
        if (caseForm == null) {
            updateCachedForm();
        }
        return caseForm;
    }

    public void saveOrUpdateForm(CaseForm caseForm) {
        CaseForm existingCaseForm = caseFormDao.getCaseForm();

        if (existingCaseForm == null) {
            Log.d(TAG, "save new case form");
            caseForm.save();
        } else {
            Log.d(TAG, "update existing case form");
            existingCaseForm.setForm(caseForm.getForm());
            existingCaseForm.update();
        }

        updateCachedForm();
    }

    private void updateCachedForm() {
        Blob form = caseFormDao.getCaseFormContent();

        String formJson = new String(form.getBlob());
        caseForm = TextUtils.isEmpty(formJson) ?
                null : new Gson().fromJson(formJson, CaseFormRoot.class);
    }

}
