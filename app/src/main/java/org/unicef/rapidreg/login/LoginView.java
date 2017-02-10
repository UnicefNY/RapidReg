package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Call;

public interface LoginView extends MvpLceView<Call<LoginResponse>> {
    void showOnlineLoginSuccessful();

    void showOfflineLoginSuccessful();

    void showLoginErrorByToast(String message);

    void showProgressDialog();

    void dismissProgressDialog();

    void showUserNameInvalid();

    void showPasswordInvalid();

    void showUrlInvalid();

    void navigateToLoginSucceedPage();

    void showCredentialErrorMsg();

    void showServerConnectionErrorMsg();

    void startTemplateFormService();
}
