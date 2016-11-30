package org.unicef.rapidreg.login;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.LoadCaseFormEvent;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.network.HttpStatusCodeHandler;
import org.unicef.rapidreg.network.NetworkStatusManager;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.utils.EncryptHelper;

import java.util.List;

import javax.inject.Inject;

import okhttp3.Headers;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public static final String TAG = LoginPresenter.class.getSimpleName();

    private UserService userService;
    private AuthService authService;

    private CompositeSubscription subscriptions;

    Context context;
    private IntentSender intentSender;

    @Inject
    public LoginPresenter(@ActivityContext Context context, UserService userService, AuthService authService) {
        this.context = context;
        this.userService = userService;
        this.authService = authService;
        intentSender = new IntentSender();
        subscriptions = new CompositeSubscription();
    }


    public String fetchURL() {
        try {
            User user = userService.getAllUsers().get(0);
            return user.getServerUrl();
        } catch (Exception e) {
            Log.w("user login", "No user ever log in successfully, so url doesn't exist!");
            return "";
        }
    }

    public void doLogin(Context context, String username, String password, String url) {
        if (!validate(context, username, password, url)) {
            return;
        }

        PrimeroConfiguration.setApiBaseUrl(url.endsWith("/") ? url : String.format("%s/", url));
        try {
            authService.init(context);
        } catch (Exception e) {
            showLoginResultMessage(e.getMessage());
        }

        showLoadingIndicator(true);
        if (NetworkStatusManager.isOnline(context)) {
            doLoginOnline(context, username, password, url);
        } else {
            doLoginOffline(context, username, password);
        }

    }

    public boolean validate(Context context, String username, String password, String url) {
        boolean nameValid = userService.isNameValid(username);
        boolean passwordValid = userService.isPasswordValid(password);
        boolean urlValid = userService.isUrlValid(url);

        getView().showUserNameError(nameValid ?
                null : context.getResources().getString(R.string.login_username_invalid_text));
        getView().showPasswordError(passwordValid ?
                null : context.getResources().getString(R.string.login_password_invalid_text));
        getView().showUrlError(urlValid ?
                null : context.getResources().getString(R.string.login_url_invalid_text));

        return nameValid && passwordValid && urlValid;
    }

    @Override
    public void attachView(LoginView view) {
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);

        subscriptions.clear();
    }

    private void doLoginOnline(final Context context, final String username,
                               final String password, final String url) {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        subscriptions.add(authService.loginRx(new LoginRequestBody(
                username,
                password,
                tm.getLine1Number(),
                androidId)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<LoginResponse>>() {
                    @Override
                    public void call(Response<LoginResponse> response) {
                        if (isViewAttached()) {
                            showLoadingIndicator(false);
                            if (response.isSuccessful()) {
                                LoginResponse loginResponse = response.body();

                                PrimeroConfiguration.setCookie(getSessionId(response.headers()));

                                User user = new User(username, EncryptHelper.encrypt(password), true, url);
                                user.setDbKey(loginResponse.getDb_key());
                                user.setOrganisation(loginResponse.getOrganization());
                                user.setLanguage(loginResponse.getLanguage());
                                user.setVerified(loginResponse.getVerified());

                                userService.setCurrentUser(user);
                                userService.saveOrUpdateUser(user);

                                goToLoginSuccessScreen();

                                EventBus.getDefault().postSticky(new LoadCaseFormEvent(PrimeroConfiguration.getCookie()));
                                EventBus.getDefault().postSticky(new LoadTracingFormEvent(PrimeroConfiguration.getCookie()));

                                showLoginResultMessage(context.getResources().getString(R.string.login_success_message));
                                Log.d(TAG, "login successful");
                            } else {
                                showLoginResultMessage(context.getResources().getString(HttpStatusCodeHandler
                                        .getHttpStatusMessage(response.code())));
                                doLoginOffline(context, username, password);

                                Log.d(TAG, "login failed");
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (isViewAttached()) {
                            showNetworkErrorMessage(throwable, false);
                            showLoadingIndicator(false);
                            doLoginOffline(context, username, password);
                        }
                    }
                }));
    }

    private void doLoginOffline(Context context, String username, String password) {
        UserService.VerifiedCode verifiedCode = userService.verify(username, password);

        showLoadingIndicator(false);
        showLoginResultMessage(context.getResources().getString(verifiedCode.getResId()));

        if (verifiedCode == UserService.VerifiedCode.OK) {
            userService.setCurrentUser(userService.getUser(username));
            goToLoginSuccessScreen();
        }
    }

    private void goToLoginSuccessScreen() {
        intentSender.showCasesActivity((Activity) context, true);
    }

    private void showLoadingIndicator(boolean active) {
        getView().showLoading(active);
    }

    private void showLoginResultMessage(String message) {
        getView().showLoginResult(message);
    }

    private void showNetworkErrorMessage(Throwable t, boolean pullToRefresh) {
        getView().showError(t, false);
    }

    private String getSessionId(Headers headers) {
        List<String> cookies = headers.values("Set-Cookie");
        for (String cookie : cookies) {
            if (cookie.contains("session_id")) {
                return cookie;
            }
        }
        Log.e(TAG, "Can not get session id");
        return null;
    }
}
