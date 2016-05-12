package org.unicef.rapidreg.login;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.model.LoginBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.network.HttpStatusCodeHandler;
import org.unicef.rapidreg.network.NetworkServiceGenerator;
import org.unicef.rapidreg.network.PrimeroClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends MvpBasePresenter<LoginView> {

    private PrimeroClient client = NetworkServiceGenerator.createService(PrimeroClient.class);

    public void doLogin(Context context, String username, String password, String url){
        if (isViewAttached()) {
            getView().showLoading(true);
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

//        Call<Response<LoginResponse>> call =
//              client.login(new LoginBody("15555215554", "qu01n23!", "primero", "android_id"));
            Call<Response<LoginResponse>> call =
                    client.login(new LoginBody(username, password, telephonyManager.getLine1Number(),
                            android_id));
            call.enqueue(new Callback<Response<LoginResponse>>() {
                @Override
                public void onResponse(Call<Response<LoginResponse>> call,
                                       Response<Response<LoginResponse>> response) {
                    if (isViewAttached()) {
                        if (response.isSuccessful()) {
                        }
                        getView().showLoading(false);
                        getView().showLoginResult(HttpStatusCodeHandler
                                .getHttpStatusMessage(response.code()));
                    }
                }

                @Override
                public void onFailure(Call<Response<LoginResponse>> call, Throwable t) {
                    if (isViewAttached()) {
                        getView().showLoading(false);
                        getView().showError(t, false);
                    }
                }
            });
        }
    }

}
