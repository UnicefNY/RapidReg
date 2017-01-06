package org.unicef.rapidreg.service;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.User;

import java.util.List;


public interface UserService {
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

    boolean isUserEverLoginSuccessfully();

    VerifiedCode verify(String username, String password);

    boolean isNameValid(String username);

    boolean isPasswordValid(String password);

    List<User> getAllUsers();

    boolean isUrlValid(String url);

    void saveOrUpdateUser(User user);

    User getUserByUserName(String username);
}
