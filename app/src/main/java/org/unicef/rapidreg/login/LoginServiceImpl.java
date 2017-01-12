package org.unicef.rapidreg.login;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.utils.EncryptHelper;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class LoginServiceImpl implements org.unicef.rapidreg.service.LoginService {
    private static final String TAG = LoginServiceImpl.class.getSimpleName();

    private ConnectivityManager connectivityManager;
    private TelephonyManager telephonyManager;

    private UserService userService;
    private AuthService authService;

    private CompositeSubscription compositeSubscription;

    public LoginServiceImpl(ConnectivityManager connectivityManager,
                            TelephonyManager telephonyManager,
                            UserService userService,
                            AuthService authService) {
        this.connectivityManager = connectivityManager;
        this.telephonyManager = telephonyManager;
        this.userService = userService;
        this.authService = authService;

        this.compositeSubscription = new CompositeSubscription();
    }

    @Override
    public boolean isOnline() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    @Override
    public void loginOnline(final String username,
                            final String password,
                            final String url,
                            String imei,
                            final LoginCallback callback) {
        authService.init();
        //TODO change hard code to be value from param
        final LoginRequestBody loginRequestBody = new LoginRequestBody(username, password, "15555215554", "8fd2274a590497e9");
        Subscription subscription = authService
                .loginRx(loginRequestBody)
                .subscribe(new Action1<Response<LoginResponse>>() {
                    @Override
                    public void call(Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            LoginResponse responseBody = response.body();
                            User user = new User(username, EncryptHelper.encrypt(password), true, url);
                            user.setDbKey(responseBody.getDb_key());
                            user.setOrganisation(responseBody.getOrganization());
                            user.setRole(responseBody.getRole());
                            user.setLanguage(responseBody.getLanguage());
                            user.setVerified(responseBody.getVerified());

                            userService.saveOrUpdateUser(user);
                            callback.onSuccessful(getSessionId(response.headers()), user);
                        } else {
                            callback.onError(response.code());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onFailed(throwable);
                    }
                });

        compositeSubscription.add(subscription);
    }

    @Override
    public void loginOffline(String username, String password, LoginCallback callback) {
        UserService.VerifiedCode verifiedCode = userService.verify(username, password, isOnline());

        if (UserService.VerifiedCode.OK == verifiedCode) {
            callback.onSuccessful("", userService.getUserByUserName(username));
        } else {
            callback.onError(verifiedCode.getResId());
        }
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

    @Override
    public void destroy() {
        compositeSubscription.clear();
    }

    @Override
    public String getServerUrl() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return "";
        }
        return users.get(0).getServerUrl();
    }

    @Override
    public boolean isUsernameValid(String username) {
        return userService.isNameValid(username);
    }

    @Override
    public boolean isPasswordValid(String password) {
        return userService.isPasswordValid(password);
    }

    @Override
    public boolean isUrlValid(String url) {
        return userService.isUrlValid(url);
    }
}
