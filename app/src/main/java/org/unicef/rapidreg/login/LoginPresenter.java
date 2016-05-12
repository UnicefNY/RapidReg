package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.model.LoginBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.network.HttpStatusCodeHandler;
import org.unicef.rapidreg.network.NetworkServiceGenerator;
import org.unicef.rapidreg.network.PrimeroClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends MvpBasePresenter<LoginView> {

    private PrimeroClient client = NetworkServiceGenerator.createService(PrimeroClient.class);

    public void doLogin(){

        Call<Response<LoginResponse>> call = client.login(new LoginBody("15555215554", "qu01n23!1", "primero", "android_id"));
        call.enqueue(new Callback<Response<LoginResponse>>() {
            @Override
            public void onResponse(Call<Response<LoginResponse>> call, Response<Response<LoginResponse>> response) {
                if (isViewAttached()) {
                    if (response.isSuccessful()) {
                    }
                    getView().showLoginResult(HttpStatusCodeHandler.getHttpStatusMessage(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Response<LoginResponse>> call, Throwable t) {
                if (isViewAttached()) {
                    getView().showError(t, false);
                }
            }
        });
    }

}
