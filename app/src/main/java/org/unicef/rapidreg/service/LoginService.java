package org.unicef.rapidreg.service;

import org.unicef.rapidreg.model.User;

public interface LoginService {
    boolean isOnline();
    void doLoginOnline(String username, String password, String url, String imei, LoginCallback callback);
    void doLoginOffline(String username, String password, LoginCallback callback);
    void destroy();
    String getServerUrl();
    boolean isUsernameValid(String username);
    boolean isPasswordValid(String password);
    boolean isUrlValid(String url);

    interface LoginCallback {
        void onLoginSuccessful(String cookie, User user);
        void onLoginFailed(Throwable error);
        void onLoginError(int code);
    }
}
