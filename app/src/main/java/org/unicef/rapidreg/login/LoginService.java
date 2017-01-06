package org.unicef.rapidreg.login;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.utils.EncryptHelper;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class LoginService {
    private static final String TAG = LoginService.class.getSimpleName();

    private ConnectivityManager connectivityManager;
    private TelephonyManager telephonyManager;

    private AuthService authService;

    private CompositeSubscription compositeSubscription;

    public LoginService(ConnectivityManager connectivityManager,
                        TelephonyManager telephonyManager,
                        AuthService authService) {
        this.connectivityManager = connectivityManager;
        this.telephonyManager = telephonyManager;
        this.authService = authService;

        this.compositeSubscription = new CompositeSubscription();
    }

    public boolean isOnline() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public void doLoginOnline(final String username,
                              final String password,
                              final String url,
                              String imei,
                              final LoginCallback callback) {
        final LoginRequestBody loginRequestBody = new LoginRequestBody(username, password, telephonyManager.getLine1Number(), imei);
        Subscription subscription = authService.loginRx(loginRequestBody).subscribe(new Action1<Response<LoginResponse>>() {
            @Override
            public void call(Response<LoginResponse> response) {
                if (response.isSuccessful()) {

                    LoginResponse responseBody = response.body();
                    User user = new User(username, EncryptHelper.encrypt(password), true, url);
                    user.setDbKey(responseBody.getDb_key());
                    user.setOrganisation(responseBody.getOrganization());
                    user.setLanguage(responseBody.getLanguage());
                    user.setVerified(responseBody.getVerified());

                    callback.onLoginSuccessful(getSessionId(response.headers()), user);
                } else {
                    callback.onLoginError(response.code());
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                callback.onLoginFailed(throwable);
            }
        });

        compositeSubscription.add(subscription);
    }

    private String getSessionId(Headers headers) {
        List<String> cookies = headers.values("Set-Cookie");
        for (String cookie : cookies) {
            if (cookie.contains("session_id")) {
                return cookie;
            }
        }
        Log.e(TAG, "Can not get session id");
        return null;
    }

    interface LoginCallback {
        void onLoginSuccessful(String cookie, User user);
        void onLoginFailed(Throwable error);
        void onLoginError(int code);
    }
}
