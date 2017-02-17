package org.unicef.rapidreg.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.model.User;

import javax.inject.Inject;

public class BasePresenter extends MvpBasePresenter<BaseView> {
    @Inject
    public BasePresenter() {}

    public User getCurrentUser() {
        return PrimeroAppConfiguration.getCurrentUser();
    }

    public void logOut() {
        PrimeroAppConfiguration.setCurrentUser(null);
        PrimeroApplication.getAppRuntime().unbindTemplateCaseService();
        PrimeroApplication.getAppRuntime().unregisterAppRuntimeReceiver();
    }
}
