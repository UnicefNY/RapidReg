package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Call;

public interface LoginView extends MvpLceView<Call<LoginResponse>> {
     void showLoginResult(String message);
     void showProgressDialog();
     void dismissProgressDialog();
     void showUserNameError(String e);
     void showPasswordError(String e);
     void showUrlError(String e);
}
