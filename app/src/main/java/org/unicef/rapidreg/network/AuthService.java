package org.unicef.rapidreg.network;


import android.content.Context;

import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.widgets.PrimeroConfiguration;

import retrofit2.Response;
import rx.Observable;


public class AuthService extends BaseRetrofitService {

    private AuthServiceInterface serviceInterface;

    @Override
    String getBaseUrl() {
        return PrimeroConfiguration.getApiBaseUrl();
    }

    public AuthService(Context context) throws Exception {
        createRetrofit(context);
        serviceInterface = getRetrofit().create(AuthServiceInterface.class);
    }

    public Observable<Response<LoginResponse>> loginRx(LoginRequestBody body) {
        return serviceInterface.login(body);
    }

    public Observable<CaseFormRoot> getFormRx(String cookie,
                                              String locale,
                                              Boolean isMobile,
                                              String parentForm) {
        return serviceInterface.getForm(cookie, locale, isMobile, parentForm);
    }

}


