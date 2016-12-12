package org.unicef.rapidreg.incident;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.UserService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class IncidentPresenter extends RecordPresenter {
    private IncidentFormService incidentFormService;
    private AuthService authService;

    @Inject
    public IncidentPresenter(UserService userService, AuthService authService,
                             IncidentFormService incidentFormService) {
        super(userService);
        this.incidentFormService = incidentFormService;
        this.authService = authService;
    }

    @Override
    public void saveForm(RecordForm recordForm, String moduleId) {
        Blob incidentFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        IncidentForm incidentForm = new IncidentForm(incidentFormBlob);
        incidentForm.setModuleId(moduleId);
        incidentFormService.saveOrUpdate(incidentForm);
    }

    public Observable<IncidentTemplateForm> loadIncidentForm(String cookie, String moduleId) {
        return authService.getIncidentForm(cookie,
                Locale.getDefault().getLanguage(), true, "incident",moduleId)
                .flatMap(new Func1<IncidentTemplateForm, Observable<IncidentTemplateForm>>() {
                    @Override
                    public Observable<IncidentTemplateForm> call(IncidentTemplateForm incidentForm) {
                        if (incidentForm == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(incidentForm);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
