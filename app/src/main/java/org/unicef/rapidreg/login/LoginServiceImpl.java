package org.unicef.rapidreg.login;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.repository.UserDao;
import org.unicef.rapidreg.service.AuthService;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.utils.EncryptHelper;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.List;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class LoginServiceImpl extends BaseRetrofitService implements org.unicef.rapidreg.service.LoginService {
    private static final String TAG = LoginServiceImpl.class.getSimpleName();

    private ConnectivityManager connectivityManager;
    private TelephonyManager telephonyManager;

    private UserDao userDao;
    private AuthService authService;

    private CompositeSubscription compositeSubscription;

    public LoginServiceImpl(ConnectivityManager connectivityManager,
                            TelephonyManager telephonyManager,
                            UserDao userDao,
                            AuthService authService) {
        this.connectivityManager = connectivityManager;
        this.telephonyManager = telephonyManager;
        this.userDao = userDao;
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
        final LoginRequestBody loginRequestBody = new LoginRequestBody(username, password, "15555215554",
                "8fd2274a590497e9");
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

                            userDao.saveOrUpdateUser(user);
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
        VerifiedCode verifiedCode = verify(username, password, isOnline());

        if (LoginService.VerifiedCode.OK == verifiedCode) {
            callback.onSuccessful("", userDao.getUser(username));
        } else {
            callback.onError(verifiedCode.getResId());
        }
    }

    public VerifiedCode verify(String username, String password, boolean isOnline) {
        User user = userDao.getUser(username);

        if (user == null) {
            return isOnline ? LoginService.VerifiedCode.PASSWORD_INCORRECT : LoginService.VerifiedCode
                    .USER_DOES_NOT_EXIST;
        }

        if (EncryptHelper.isMatched(password, user.getPassword())) {
            return LoginService.VerifiedCode.OK;
        } else {
            return LoginService.VerifiedCode.PASSWORD_INCORRECT;
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
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            return "";
        }
        return users.get(0).getServerUrl();
    }

    @Override
    public boolean isUsernameValid(String username) {
        return username != null && username.matches("\\A[^ ]+\\z");
    }

    @Override
    public boolean isPasswordValid(String password) {
        return password != null && password.matches("\\A(?=.*[a-zA-Z])(?=.*[0-9]).{8,}\\z");
    }

    @Override
    public boolean isUrlValid(String url) {
        return !TextUtils.isEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
