package org.unicef.rapidreg.tracing;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.RecordConfiguration;
import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.TracingFormService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TracingPresenter extends RecordPresenter {

    private TracingFormService tracingFormService;
    private AuthService authService;

    @Inject
    public TracingPresenter(AuthService authService, TracingFormService tracingFormService) {
        this.authService = authService;
        this.tracingFormService = tracingFormService;
    }

    @Override
    public void saveForm(RecordForm recordForm, String moduleId) {
        Blob tracingFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        TracingForm tracingForm = new TracingForm(tracingFormBlob);
        tracingFormService.saveOrUpdateForm(tracingForm);
    }

    public void loadTracingForm(String cookie) {
        authService.getTracingForm(cookie,
                Locale.getDefault().getLanguage(), true, "tracing_request", "primeromodule-cp")
                .subscribe(new Action1<TracingTemplateForm>() {
                    @Override
                    public void call(TracingTemplateForm tracingTemplateForm) {
                        saveForm(tracingTemplateForm, RecordConfiguration.MODULE_ID_CP);
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
