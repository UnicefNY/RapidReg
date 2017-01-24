package org.unicef.rapidreg.service;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.User;

public interface LoginService {
    boolean isOnline();

    void loginOnline(String username, String password, String url, String imei, LoginCallback callback);

    void loginOffline(String username, String password, String url, LoginCallback callback);

    void destroy();

    String getServerUrl();

    boolean isUsernameValid(String username);

    boolean isPasswordValid(String password);

    boolean isUrlValid(String url);

    enum VerifiedCode {
        OFFLINE_USER_DOES_NOT_EXIST,
        OFFLINE_PASSWORD_INCORRECT,
        OK
    }

    interface LoginCallback {
        void onSuccessful(String cookie, User user);

        void onFailed(Throwable error);

        void onError(int code);
    }
}
