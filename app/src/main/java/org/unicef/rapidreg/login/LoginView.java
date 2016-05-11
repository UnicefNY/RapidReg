package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface LoginView extends MvpView{
    void showLoginSuccessMessages(String messages);
    void showLoginFailedMessages(String messages);
}
