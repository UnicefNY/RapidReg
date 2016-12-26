package org.unicef.rapidreg.incident;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.IncidentFormService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class IncidentPresenter extends RecordPresenter {
    private IncidentFormService incidentFormService;
    private AuthService authService;

    @Inject
    public IncidentPresenter(AuthService authService,
                             IncidentFormService incidentFormService) {
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


    public void loadIncidentForm(String cookie, final String moduleId) {
        authService.getIncidentForm(cookie,
                Locale.getDefault().getLanguage(), true, "incident", moduleId)
                .subscribe(new Action1<IncidentTemplateForm>() {
                    @Override
                    public void call(IncidentTemplateForm incidentForm) {
                        saveForm(incidentForm, moduleId);
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
