package org.unicef.rapidreg.childcase;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.UserService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
        org.unicef.rapidreg.model.CaseForm caseForm = new org.unicef.rapidreg.model.CaseForm(caseFormBlob);
        caseForm.setModuleId(moduleId);
        caseFormService.saveOrUpdate(caseForm);
    }

    public void loadCaseForm(String cookie, final String moduleId) {
        authService.getCaseForm(cookie,
                Locale.getDefault().getLanguage(), true, "case", moduleId)
                .flatMap(new Func1<CaseTemplateForm, Observable<CaseTemplateForm>>() {
                    @Override
                    public Observable<CaseTemplateForm> call(CaseTemplateForm caseForm) {
                        if (caseForm == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(caseForm);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CaseTemplateForm>() {
                    @Override
                    public void call(CaseTemplateForm caseForm) {
                        saveForm(caseForm, moduleId);
                        ((CaseActivity) getView()).setFormSyncFail(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ((CaseActivity) getView()).setFormSyncFail(true);
                        if (isViewAttached()) {
                            ((CaseView) getView()).promoteSyncCaseFail();
                        }
                    }
                });

    }
}
