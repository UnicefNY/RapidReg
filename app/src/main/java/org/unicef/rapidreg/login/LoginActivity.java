package org.unicef.rapidreg.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.injection.component.DaggerActivityComponent;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.network.NetworkStatusManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class LoginActivity extends MvpActivity<LoginView, LoginPresenter> implements LoginView {

    public static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login)
    Button loginButton;

    @BindView(R.id.username)
    EditText usernameEditView;

    @BindView(R.id.password)
    EditText passwordEditView;

    @BindView(R.id.url)
    EditText urlEditView;

    @BindView(R.id.change_url)
    TextView changeUrlTextView;

    @Inject
    LoginPresenter loginPresenter;

    private ProgressDialog loginProgressDialog;

    ActivityComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(PrimeroApplication.get(this).getComponent())
                .activityModule(new ActivityModule(this))
                .build();

        activityComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        hideUrlInputIfUserEverLoginSuccessfully();
        loginProgressDialog = new ProgressDialog(this);


        usernameEditView.requestFocus();

    }

    @OnClick(R.id.login)
    public void onLoginButtonClicked() {
        presenter.doLogin(this,
                usernameEditView.getText().toString().trim(),
                passwordEditView.getText().toString().trim(),
                urlEditView.getText().toString().trim());
    }

    @OnClick(R.id.change_url)
    public void onChangeUrlTextClicked() {

        if (!NetworkStatusManager.isOnline(this)) {
            Toast.makeText(this, "Network is not accessible!", Toast.LENGTH_LONG).show();
            return;
        }
        changeUrlTextView.setVisibility(View.INVISIBLE);
        urlEditView.setVisibility(View.VISIBLE);
        urlEditView.requestFocus();
        urlEditView.setSelection(0, urlEditView.length());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void hideUrlInputIfUserEverLoginSuccessfully() {
        changeUrlTextView.setVisibility(View.INVISIBLE);
        try {
            String url = fetchHistoryURL();
            urlEditView.setText(url);

            if (!"".equals(url)) {
                urlEditView.setVisibility(View.GONE);
                changeUrlTextView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            // do nothing while table doesn't exist
        }
    }

    private String fetchHistoryURL() {
        String url = urlEditView.getText().toString().trim();
        return "".equals(url) ? presenter.fetchURL() : url;
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return loginPresenter;
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
        usernameEditView.setError(e);
    }

    @Override
    public void showPasswordError(String e) {
        passwordEditView.setError(e);
    }

    @Override
    public void showUrlError(String e) {
        urlEditView.setError(e);
    }

}
