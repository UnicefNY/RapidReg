package org.unicef.rapidreg.network;


import android.content.Context;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;

import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class AuthService extends BaseRetrofitService {

    private AuthServiceInterface serviceInterface;

    @Override
    String getBaseUrl() {
        return PrimeroConfiguration.getApiBaseUrl();
    }

    public void init(Context context) throws Exception {
        createRetrofit(context);
        serviceInterface = getRetrofit().create(AuthServiceInterface.class);
    }

    public Observable<Response<LoginResponse>> loginRx(LoginRequestBody body) {
        return serviceInterface.login(body);
    }

    public Observable<CaseTemplateForm> getCaseForm(String cookie, String locale,
                  Boolean isMobile, String parentForm, String moduleId) {
        return serviceInterface.getCaseForm(cookie, locale, isMobile, parentForm, moduleId)
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
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TracingTemplateForm> getTracingForm(String cookie, String locale,
                  Boolean isMobile, String parentForm, String moduleId) {
        return serviceInterface.getTracingForm(cookie, locale, isMobile, parentForm, moduleId);
    }

    public Observable<IncidentTemplateForm> getIncidentForm(String cookie, String locale,
                       Boolean isMobile, String parentForm, String moduleId) {
        return serviceInterface.getIncidentForm(cookie, locale, isMobile, parentForm, moduleId);
    }
}


