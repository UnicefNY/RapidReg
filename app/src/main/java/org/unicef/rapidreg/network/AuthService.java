package org.unicef.rapidreg.network;


import android.content.Context;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Response;
import rx.Observable;


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

    public Observable<CaseTemplateForm> getCaseForm(String cookie, String locale, Boolean isMobile, String parentForm,
                                                    String moduleId) {
        return serviceInterface.getCaseForm(cookie, locale, isMobile, parentForm, moduleId);
    }

    public Observable<TracingTemplateForm> getTracingForm(String cookie, String locale, Boolean isMobile, String
            parentForm, String moduleId) {
        return serviceInterface.getTracingForm(cookie, locale, isMobile, parentForm, moduleId);
    }
}


