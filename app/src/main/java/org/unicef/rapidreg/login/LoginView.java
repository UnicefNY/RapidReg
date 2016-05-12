package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Call;

public interface LoginView extends MvpLceView<Call<LoginResponse>> {
    public void showLoginResult(String message);
    public void showProgressDialog();
    public void dismissProgressDialog();
}
