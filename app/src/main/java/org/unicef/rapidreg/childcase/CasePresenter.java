package org.unicef.rapidreg.childcase;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.FormRemoteService;

import javax.inject.Inject;

public class CasePresenter extends RecordPresenter {
    private CaseFormService caseFormService;
    private FormRemoteService formRemoteService;

    @Inject
    public CasePresenter(FormRemoteService authService, CaseFormService caseFormService) {
        this.caseFormService = caseFormService;
        this.formRemoteService = authService;
    }

    @Override
    public void saveForm(RecordForm recordForm, String moduleId) {
        Blob caseFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        CaseForm caseForm = new CaseForm(caseFormBlob);
        caseForm.setModuleId(moduleId);
        caseFormService.saveOrUpdate(caseForm);
    }

    public void loadCaseForm(final String moduleId) {
        formRemoteService.getCaseForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getDefaultLanguage
                (), true, PrimeroAppConfiguration.PARENT_CASE, moduleId)
                .subscribe(caseForm -> {
                    saveForm(caseForm, moduleId);
                    setFormSyncFail(false);
                }, throwable -> {
                    if (isViewAttached()) {
                        getView().promoteSyncFormsError();
                    }
                    setFormSyncFail(true);
                });

    }
}
