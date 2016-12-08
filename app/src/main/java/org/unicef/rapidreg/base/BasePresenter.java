package org.unicef.rapidreg.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.UserService;

import javax.inject.Inject;

public class BasePresenter extends MvpBasePresenter<BaseView> {
    @Inject
    public BasePresenter() {}

    public User getCurrentUser() {
        return PrimeroConfiguration.getCurrentUser();
    }

    public void logOut() {
        PrimeroConfiguration.setCurrentUser(null);
    }

}
