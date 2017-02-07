package org.unicef.rapidreg.service.impl;

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
import org.unicef.rapidreg.repository.remote.LoginRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.utils.EncryptHelper;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.List;

import okhttp3.Headers;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginServiceImpl extends BaseRetrofitService<LoginRepository> implements org.unicef.rapidreg.service
        .LoginService {
    private static final String TAG = LoginServiceImpl.class.getSimpleName();

    private ConnectivityManager connectivityManager;
    private TelephonyManager telephonyManager;

    private UserDao userDao;

    private CompositeSubscription compositeSubscription;

    public LoginServiceImpl(ConnectivityManager connectivityManager,
                            TelephonyManager telephonyManager,
                            UserDao userDao) {
        this.connectivityManager = connectivityManager;
        this.telephonyManager = telephonyManager;
        this.userDao = userDao;
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
        //TODO change hard code to be value from param
        final LoginRequestBody loginRequestBody = new LoginRequestBody(username, password, "15555215554",
                "8fd2274a590497e9");
        Subscription subscription = getRepository(LoginRepository.class)
                .login(loginRequestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        LoginResponse responseBody = response.body();
                        User user = new User(username, EncryptHelper.encrypt(password), true, TextUtils.lintUrl
                                (url));
                        user.setDbKey(responseBody.getDbKey());
                        user.setOrganisation(responseBody.getOrganization());
                        user.setRole(responseBody.getRole());
                        user.setLanguage(responseBody.getLanguage());
                        user.setVerified(responseBody.getVerified());

                        userDao.saveOrUpdateUser(user);
                        callback.onSuccessful(getSessionId(response.headers()), user);
                    } else {
                        callback.onError(response.code());
                    }
                }, throwable -> {
                    callback.onFailed(throwable);
                });

        compositeSubscription.add(subscription);
    }

    @Override
    public void loginOffline(String username, String password, String url, LoginCallback callback) {
        VerifiedCode verifiedCode = verify(username, password, url);

        if (LoginService.VerifiedCode.OK == verifiedCode) {
            callback.onSuccessful("", userDao.getUser(username, url));
        } else if (VerifiedCode.OFFLINE_PASSWORD_INCORRECT == verifiedCode) {
            callback.onError(verifiedCode.ordinal());
        } else {
            callback.onFailed(null);
        }
    }

    public VerifiedCode verify(String username, String password, String url) {
        User user = userDao.getUser(username, url);
        if (user == null) {
            return LoginService.VerifiedCode.OFFLINE_USER_DOES_NOT_EXIST;
        }

        if (EncryptHelper.isMatched(password, user.getPassword())) {
            return LoginService.VerifiedCode.OK;
        }
        return LoginService.VerifiedCode.OFFLINE_PASSWORD_INCORRECT;
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
