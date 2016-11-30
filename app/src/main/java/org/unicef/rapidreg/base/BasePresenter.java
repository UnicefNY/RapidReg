package org.unicef.rapidreg.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.UserService;

import javax.inject.Inject;

public class BasePresenter extends MvpBasePresenter<BaseView> {

    private UserService userService;

    @Inject
    public BasePresenter(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    public void setCurrentUser(User currentUser) {
        userService.setCurrentUser(currentUser);
    }

}
