package org.unicef.rapidreg.service;

import org.unicef.rapidreg.model.User;

public interface LoginService {
    boolean isOnline();

    void loginOnline(String username, String password, String url, String imei, LoginCallback callback);

    void loginOffline(String username, String password, String url, LoginCallback callback);

    void destroy();

    String loadLastLoginServerUrl();

    boolean isUsernameValid(String username);

    boolean isPasswordValid(String password);

    boolean isUrlValid(String url);

    interface LoginCallback {
        void onSuccessful(String cookie, User user);

        void onFailed(Throwable error);

        void onError();
    }
}
