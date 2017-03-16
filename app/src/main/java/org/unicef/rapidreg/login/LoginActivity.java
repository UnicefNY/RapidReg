package org.unicef.rapidreg.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.unicef.rapidreg.AppRuntime;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseProgressDialog;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.injection.component.DaggerActivityComponent;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.utils.Utils;

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

    private BaseProgressDialog loginProgressDialog;
    private ActivityComponent activityComponent;

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
        loginProgressDialog = new BaseProgressDialog(this, R.style.ProgressDialogTheme);

        usernameEditView.requestFocus();
    }

    @OnClick(R.id.login)
    public void onLoginButtonClicked() {
        presenter.doLogin(
                usernameEditView.getText().toString().trim(),
                passwordEditView.getText().toString().trim(),
                urlEditView.getText().toString().trim(),
                PrimeroAppConfiguration.getAndroidId());
    }

    @OnClick(R.id.change_url)
    public void onChangeUrlTextClicked() {
        changeUrlTextView.setVisibility(View.GONE);
        urlEditView.setVisibility(View.VISIBLE);
        urlEditView.requestFocus();
        urlEditView.setSelection(0, urlEditView.length());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void hideUrlInputIfUserEverLoginSuccessfully() {
        changeUrlTextView.setVisibility(View.GONE);
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
        return "".equals(url) ? presenter.loadLastLoginUrl() : url;
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
        Utils.showMessageByToast(this, getResources().getString(R.string.login_failed_to_connect_prefix) +
                PrimeroAppConfiguration.getApiBaseUrl(), Toast.LENGTH_SHORT);
        Utils.showMessageByToast(this, R.string.login_failed_check_network_and_url, Toast.LENGTH_SHORT);
    }

    @Override
    public void setData(Call<LoginResponse> data) {
    }

    @Override
    public void loadData(boolean pullToRefresh) {

    }

    @Override
    public void showOnlineLoginSuccessful() {
        Utils.showMessageByToast(this, R.string.login_success_message, Toast.LENGTH_SHORT);
    }

    @Override
    public void showOfflineLoginSuccessful() {
        Utils.showMessageByToast(this, R.string.login_offline_success_text, Toast.LENGTH_SHORT);
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
    public void showLoginErrorByToast(String message) {
        Utils.showMessageByToast(this,  message, Toast.LENGTH_SHORT);
    }

    @Override
    public void showUserNameInvalid() {
        usernameEditView.setError(getResources().getString(R.string.login_username_invalid_text));
    }

    @Override
    public void showPasswordInvalid() {
        passwordEditView.setError(getResources().getString(R.string.login_password_invalid_text));
    }

    @Override
    public void showUrlInvalid() {
        urlEditView.setError(getResources().getString(R.string.login_url_invalid_text));
    }

    @Override
    public void navigateToLoginSucceedPage() {
        new IntentSender().showCasesActivity(this, true,true);
    }

    @Override
    public void showCredentialErrorMsg() {
        Utils.showMessageByToast(this, R.string.login_failed_credential, Toast.LENGTH_LONG);
    }

    @Override
    public void showServerConnectionErrorMsg() {
        Utils.showMessageByToast(this, getResources().getString(R.string.login_failed_to_connect_prefix) +
                PrimeroAppConfiguration.getApiBaseUrl(), Toast.LENGTH_SHORT);
        Utils.showMessageByToast(this, R.string.login_failed_check_network_and_url, Toast.LENGTH_SHORT);
    }

    @Override
    public void configAppRuntimeEvent() {
        AppRuntime appRuntime = PrimeroApplication.getAppRuntime();
        appRuntime.bindTemplateCaseService();
        appRuntime.registerAppRuntimeReceiver();
    }
}
