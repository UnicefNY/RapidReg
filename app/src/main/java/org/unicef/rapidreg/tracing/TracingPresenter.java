package org.unicef.rapidreg.tracing;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.RecordPresenter;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.TracingFormRoot;
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
    public TracingPresenter(UserService userService, AuthService authService, TracingFormService tracingFormService) {
        super(userService);
        this.authService = authService;
        this.tracingFormService = tracingFormService;
    }

    @Override
    public void saveForm(RecordForm recordForm) {
        Blob tracingFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        TracingForm tracingForm = new TracingForm(tracingFormBlob);
        tracingFormService.saveOrUpdateForm(tracingForm);
    }

    public Observable<TracingFormRoot> loadTracingForm(String cookie) {
        return authService.getTracingFormRx(cookie,
                Locale.getDefault().getLanguage(), true, "tracing_request")
                .flatMap(new Func1<TracingFormRoot, Observable<TracingFormRoot>>() {
                    @Override
                    public Observable<TracingFormRoot> call(TracingFormRoot tracingFormRoot) {
                        if (tracingFormRoot == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(tracingFormRoot);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
