package org.unicef.rapidreg.login;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.network.HttpStatusCodeHandler;
import org.unicef.rapidreg.network.NetworkServiceGenerator;
import org.unicef.rapidreg.network.PrimeroClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public static final String TAG = LoginPresenter.class.getSimpleName();
    private PrimeroClient client;

    public void doLogin(Context context, String username, String password, String url) {
        if (isViewAttached()) {
            getView().showLoading(true);
            doLoginOnline(context, username, password);
        }
    }

    private void doLoginOnline(Context context, String username, String password) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        try {
            client = NetworkServiceGenerator.createService(context, PrimeroClient.class);
        } catch (Exception e) {
            getView().showLoginResult(e.getMessage());
            Log.e(TAG, e.getMessage());
            return;
        }

        Call<Response<LoginResponse>> call = client.login(
                new LoginRequestBody(username,
                        password,
                        telephonyManager.getLine1Number(),
                        android_id));

        call.enqueue(new Callback<Response<LoginResponse>>() {
            @Override
            public void onResponse(Call<Response<LoginResponse>> call,
                                   Response<Response<LoginResponse>> response) {
                if (isViewAttached()) {
                    getView().showLoading(false);
                    if (response.isSuccessful()) {
                        getView().showLoginResult(HttpStatusCodeHandler.LOGIN_SUCCESS_MESSAGE);
                    } else {
                        getView().showLoginResult(HttpStatusCodeHandler
                                .getHttpStatusMessage(response.code()));
                    }
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
