package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.model.LoginBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.network.NetworkServiceGenerator;
import org.unicef.rapidreg.network.PrimeroClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends MvpBasePresenter<LoginView> {

    private PrimeroClient client = NetworkServiceGenerator.createService(PrimeroClient.class);
    public static final String LOGIN_SUCCESS_MESSAGE = "Login success!";
    public static final String LOGIN_FAILED_MESSAGE = "Login failed!";

    public void doLogin(){

        Call<LoginResponse> call = client.login(new LoginBody("15555215554", "qu01n23!", "primero", "android_id"));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (isViewAttached()) {
                    getView().showContent();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (isViewAttached()) {
                    getView().showError(t, false);
                }
            }
        });
    }

}
