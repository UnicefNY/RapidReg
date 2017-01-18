package org.unicef.rapidreg.service.impl;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.repository.remote.AuthRepository;
import org.unicef.rapidreg.service.AuthService;
import org.unicef.rapidreg.service.BaseRetrofitService;

import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class AuthServiceImpl extends BaseRetrofitService implements AuthService{
    private AuthRepository authRepository;

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }

    public void init() {
        authRepository = createRetrofit().create(AuthRepository.class);
    }

    public Observable<Response<LoginResponse>> loginRx(LoginRequestBody body) {
        return authRepository.login(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CaseTemplateForm> getCaseForm(String cookie, String locale,
                  Boolean isMobile, String parentForm, String moduleId) {
        return authRepository.getCaseForm(cookie, locale, isMobile, parentForm, moduleId)
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TracingTemplateForm> getTracingForm(String cookie, String locale,
                  Boolean isMobile, String parentForm, String moduleId) {
        return authRepository.getTracingForm(cookie, locale, isMobile, parentForm, moduleId)
                .flatMap(new Func1<TracingTemplateForm, Observable<TracingTemplateForm>>() {
                    @Override
                    public Observable<TracingTemplateForm> call(TracingTemplateForm
                                                                        tracingTemplateForm) {
                        if (tracingTemplateForm == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(tracingTemplateForm);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<IncidentTemplateForm> getIncidentForm(String cookie, String locale,
                       Boolean isMobile, String parentForm, String moduleId) {
        return authRepository.getIncidentForm(cookie, locale, isMobile, parentForm, moduleId)
                .flatMap(new Func1<IncidentTemplateForm, Observable<IncidentTemplateForm>>() {
                    @Override
                    public Observable<IncidentTemplateForm> call(IncidentTemplateForm
                                                                         incidentForm) {
                        if (incidentForm == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(incidentForm);
                    }
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}


