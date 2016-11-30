package org.unicef.rapidreg.childcase;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.UserService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CasePresenter extends RecordPresenter {

    private CaseFormService caseFormService;
    private AuthService authService;

    @Inject
    public CasePresenter(UserService userService, AuthService authService, CaseFormService caseFormService) {
        super(userService);
        this.caseFormService = caseFormService;
        this.authService = authService;
    }

    @Override
    public void saveForm(RecordForm recordForm) {
        Blob caseFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        CaseForm caseForm = new CaseForm(caseFormBlob);
        caseFormService.saveOrUpdateForm(caseForm);
    }

    public Observable<CaseFormRoot> loadCaseForm(String cookie) {
        return authService.getCaseFormRx(cookie,
                Locale.getDefault().getLanguage(), true, "case")
                .flatMap(new Func1<CaseFormRoot, Observable<CaseFormRoot>>() {
                    @Override
                    public Observable<CaseFormRoot> call(CaseFormRoot caseFormRoot) {
                        if (caseFormRoot == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(caseFormRoot);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
