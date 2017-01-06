package org.unicef.rapidreg.service;

import org.unicef.rapidreg.model.User;

public interface LoginService {
    int VALID = 0;
    int INVALID_USERNAME = -1;
    int INVALID_PASSWORD = -2;
    int INVALID_URL = -3;

    boolean isOnline();
    void doLoginOnline(String username, String password, String url, String imei, LoginCallback callback);
    void doLoginOffline(String username, String password, LoginCallback callback);
    void destroy();
    String getServerUrl();
    int validate(String username, String password, String url);

    interface LoginCallback {
        void onLoginSuccessful(String cookie, User user);
        void onLoginFailed(Throwable error);
        void onLoginError(int code);
    }
}
