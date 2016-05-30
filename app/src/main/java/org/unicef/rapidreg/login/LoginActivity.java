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

public class LoginActivity extends MvpActivity<LoginView, LoginPresenter> implements LoginView {

    @BindView(R.id.button_login)
    Button loginButton;
    @BindView(R.id.editview_username)
    EditText usernameEditview;
    @BindView(R.id.editview_password)
    EditText passwordEditview;
    @BindView(R.id.editview_url)
    EditText urlEditview;
    private ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginProgressDialog = new ProgressDialog(this);
    }

    @OnClick(R.id.button_login)
    public void onLoginButtonClicked() {
        presenter.doLogin(this,
                usernameEditview.getText().toString().trim(),
                passwordEditview.getText().toString().trim(),
                urlEditview.getText().toString().trim());
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void showLoading(boolean active) {
        if (active) {
            showProgressDialog();
        } else {
            dismissProgressDialog();
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

    @Override
    public void showProgressDialog() {
        loginProgressDialog.setMessage(getResources().getString(R.string.login_loading_text));
        loginProgressDialog.setCancelable(false);
        loginProgressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        loginProgressDialog.dismiss();
    }

    @Override
    public void showUserNameError(String e) {
        usernameEditview.setError(e);
    }

    @Override
    public void showPasswordError(String e) {
        passwordEditview.setError(e);
    }

    @Override
    public void showUrlError(String e) {
        urlEditview.setError(e);
    }
}
