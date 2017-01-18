package org.unicef.rapidreg.service.impl;

import org.unicef.rapidreg.repository.UserDao;
import org.unicef.rapidreg.service.UserService;


public class UserServiceImpl implements UserService {
    public static final String TAG = UserServiceImpl.class.getSimpleName();

    private UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean isUserEverLoginSuccessfully() {
        return userDao.countUser() > 0;
    }
}
