package org.unicef.rapidreg.service.impl;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.RecordConfiguration;
import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.service.CaseFormService;

public class CaseFormServiceImpl implements CaseFormService {
    public static final String TAG = CaseFormService.class.getSimpleName();
    private CaseFormDao caseFormDao;

    public CaseFormServiceImpl(CaseFormDao caseFormDao) {
        this.caseFormDao = caseFormDao;
    }

    public boolean isReady() {
        return caseFormDao.getCaseForm(RecordConfiguration.MODULE_ID_CP) != null && caseFormDao.getCaseForm(RecordConfiguration.MODULE_ID_CP).getForm() != null
                && caseFormDao.getCaseForm(RecordConfiguration.MODULE_ID_GBV) != null && caseFormDao.getCaseForm(RecordConfiguration.MODULE_ID_GBV).getForm() != null;
    }

    public CaseTemplateForm getCPTemplate() {
        Blob form = caseFormDao.getCaseForm(RecordConfiguration.MODULE_ID_CP).getForm();
        return getCaseTemplateForm(form);
    }

    @Override
    public CaseTemplateForm getGBVTemplate() {
        Blob form = caseFormDao.getCaseForm(RecordConfiguration.MODULE_ID_GBV).getForm();
        return getCaseTemplateForm(form);
    }

    private CaseTemplateForm getCaseTemplateForm(Blob form) {
        String formJson = new String(form.getBlob());
        CaseTemplateForm caseForm = TextUtils.isEmpty(formJson) ?
                null : new Gson().fromJson(formJson, CaseTemplateForm.class);
        return caseForm;
    }

    public void saveOrUpdate(org.unicef.rapidreg.model.CaseForm caseForm) {
        CaseForm existingCaseForm = caseFormDao.getCaseForm(caseForm.getModuleId());
        if (existingCaseForm == null) {
            caseForm.save();
        } else {
            existingCaseForm.setForm(caseForm.getForm());
            existingCaseForm.update();
        }
    }
}
