package org.unicef.rapidreg.tracing;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.UserService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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

    public Observable<TracingTemplateForm> loadTracingForm(String cookie) {
        return authService.getTracingForm(cookie,
                Locale.getDefault().getLanguage(), true, "tracing_request", "primeromodule-cp")
                .flatMap(new Func1<TracingTemplateForm, Observable<TracingTemplateForm>>() {
                    @Override
                    public Observable<TracingTemplateForm> call(TracingTemplateForm tracingTemplateForm) {
                        if (tracingTemplateForm == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(tracingTemplateForm);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
