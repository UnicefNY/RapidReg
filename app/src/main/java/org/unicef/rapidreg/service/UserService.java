package org.unicef.rapidreg.service;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.db.UserDao;
import org.unicef.rapidreg.db.impl.UserDaoImpl;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.EncryptHelper;

public class UserService {
    public enum VerifiedCode {
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

    private UserDao userDao;

    public static UserService getInstance() {
        return new UserService(new UserDaoImpl());
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isUserEverLoginSuccessfully() {
        return userDao.countUser() > 0;
    }

    public VerifiedCode verify(String username, String password) {
        User user = userDao.getUser(username);

        if (user == null) {
            return VerifiedCode.USER_DOES_NOT_EXIST;
        }

        if (EncryptHelper.isMatched(password, user.getPassword())) {
            return VerifiedCode.OK;
        } else {
            return VerifiedCode.PASSWORD_INCORRECT;
        }
    }
}
