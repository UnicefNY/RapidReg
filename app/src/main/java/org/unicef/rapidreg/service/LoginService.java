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
        USER_DOES_NOT_EXIST(R.string.login_offline_no_user_text),
        PASSWORD_INCORRECT(R.string.login_failed_text),
        OK(R.string.login_offline_success_text);

        private int resId;

        VerifiedCode(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }

    interface LoginCallback {
        void onSuccessful(String cookie, User user);

        void onFailed(Throwable error);

        void onError(int code);
    }
}
