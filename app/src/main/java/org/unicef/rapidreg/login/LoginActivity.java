package org.unicef.rapidreg.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.LoginResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class LoginActivity extends MvpActivity<LoginView, LoginPresenter> implements LoginView{

    @BindView(R.id.button_login) Button loginButton;
    @BindView(R.id.editview_username) EditText usernameEditview;
    @BindView(R.id.editview_password) EditText passwordEditview;
    @BindView(R.id.editview_url) EditText urlEditview;
    private ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginProgressDialog = new ProgressDialog(this);
    }

    @OnClick(R.id.button_login)
    public void onLoginButtonClicked() {
        presenter.doLogin(this,
                            usernameEditview.getText().toString(),
                            passwordEditview.getText().toString(),
                            urlEditview.getText().toString());
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        if (pullToRefresh) {
            loginProgressDialog.setMessage(getResources().getString(R.string.loading_login_text));
            loginProgressDialog.setCancelable(false);
            loginProgressDialog.show();
        } else {
            loginProgressDialog.hide();
        }
    }

    @Override
    public void showContent() {
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(Call<LoginResponse> data) {

    }

    @Override
    public void loadData(boolean pullToRefresh) {

    }

    @Override
    public void showLoginResult(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
