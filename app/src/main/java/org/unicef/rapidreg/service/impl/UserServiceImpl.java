package org.unicef.rapidreg.service.impl;

import android.util.Log;
import android.util.Patterns;

import org.unicef.rapidreg.db.UserDao;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.utils.EncryptHelper;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.List;


public class UserServiceImpl implements UserService {
    public static final String TAG = UserServiceImpl.class.getSimpleName();

    private UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isUserEverLoginSuccessfully() {
        return userDao.countUser() > 0;
    }

    public VerifiedCode verify(String username, String password, boolean isOnline) {
        User user = userDao.getUser(username);

        if (user == null) {
            return isOnline ? VerifiedCode.PASSWORD_INCORRECT : VerifiedCode.USER_DOES_NOT_EXIST;
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
        return !TextUtils.isEmpty(url) && isMatches(url);
    }

    private boolean isMatches(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
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

    public User getUserByUserName(String username) {
        return userDao.getUser(username);
    }
}
