package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Call;

public interface LoginView extends MvpLceView<Call<LoginResponse>> {
    void showLoginSuccessful();

    void showLoginErrorByToast(String message);

    void showLoginErrorByResId(int resId);

    void showProgressDialog();

    void dismissProgressDialog();

    void showUserNameInvalid();

    void showPasswordInvalid();

    void showUrlInvalid();

    void goToLoginSuccessScreen();
}
