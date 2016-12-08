package org.unicef.rapidreg.service;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.db.UserDao;
import org.unicef.rapidreg.db.impl.UserDaoImpl;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.EncryptHelper;

import java.util.List;

import javax.inject.Inject;

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

    public static final String TAG = UserService.class.getSimpleName();

    private UserDao userDao;
    private User currentUser;

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

    public boolean isNameValid(String username) {
        return username != null && username.matches("\\A[^ ]+\\z");
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.matches("\\A(?=.*[a-zA-Z])(?=.*[0-9]).{8,}\\z");
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public boolean isUrlValid(String url) {
        return !TextUtils.isEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }

    public void saveOrUpdateUser(User user) {
        User existingUser = userDao.getUser(user.getUsername());

        if (existingUser == null) {
            Log.d(TAG, "save new user");
            user.save();
        } else {
            Log.d(TAG, "update existing user");
            existingUser.updateFields(user);
            existingUser.update();
        }
    }
    public User getUser(String username) {
        return userDao.getUser(username);
    }
}
