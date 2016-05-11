package org.unicef.rapidreg.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.unicef.rapidreg.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends MvpActivity<LoginView, LoginPresenter> implements LoginView{

    @BindView(R.id.login_button) Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClicked() {
        presenter.doLogin();
    }

    @Override
    public void showLoginSuccessMessages(String messages) {
        Toast.makeText(LoginActivity.this, messages, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoginFailedMessages(String messages) {
        Toast.makeText(LoginActivity.this, messages, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }
}
