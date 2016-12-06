package org.unicef.rapidreg.network;


import android.content.Context;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.TracingFormRoot;
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

    public Observable<CaseFormRoot> getCaseFormRx(String cookie,
                                                  String locale,
                                                  Boolean isMobile,
                                                  String parentForm) {
        return serviceInterface.getCaseForm(cookie, locale, isMobile, parentForm);
    }

    public Observable<TracingFormRoot> getTracingFormRx(String cookie,
                                                        String locale,
                                                        Boolean isMobile,
                                                        String parentForm) {
        return serviceInterface.getTracingForm(cookie, locale, isMobile, parentForm);
    }
}


