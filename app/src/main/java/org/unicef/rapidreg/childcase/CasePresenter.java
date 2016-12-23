package org.unicef.rapidreg.childcase;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.CaseFormService;
import javax.inject.Inject;
import rx.functions.Action1;

public class CasePresenter extends RecordPresenter {
    private CaseFormService caseFormService;
    private AuthService authService;

    @Inject
    public CasePresenter(AuthService authService, CaseFormService caseFormService) {
        this.caseFormService = caseFormService;
        this.authService = authService;
    }

    @Override
    public void saveForm(RecordForm recordForm, String moduleId) {
        Blob caseFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        CaseForm caseForm = new CaseForm(caseFormBlob);
        caseForm.setModuleId(moduleId);
        caseFormService.saveOrUpdate(caseForm);
    }

    public void loadCaseForm(String language, String cookie, final String moduleId) {
        authService.getCaseForm(cookie, language, true, "case", moduleId)
                .subscribe(new Action1<CaseTemplateForm>() {
                    @Override
                    public void call(CaseTemplateForm caseForm) {
                        saveForm(caseForm, moduleId);
                        setFormSyncFail(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (isViewAttached()) {
                            getView().promoteSyncFormsError();
                        }
                        setFormSyncFail(true);
                    }
                });

    }
}
