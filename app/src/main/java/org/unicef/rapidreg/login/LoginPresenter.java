package org.unicef.rapidreg.login;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVIncidentFormEvent;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
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
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginService loginService;
    private UserService userService;
    private AuthService authService;

    private CompositeSubscription subscriptions;

    @Inject
    public LoginPresenter(LoginService loginService, UserService userService, AuthService authService) {
        this.loginService = loginService;
        this.userService = userService;
        this.authService = authService;
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

    public void doLogin(String username, String password, String url) {
        if (!validate(username, password, url)) {
            return;
        }

        PrimeroConfiguration.setApiBaseUrl(url.endsWith("/") ? url : String.format("%s/", url));
        try {
            authService.init();
        } catch (Exception e) {
            getView().showErrorByToast(e.getMessage());
        }

        showLoadingIndicator(true);
        if (loginService.isOnline()) {
            doLoginOnline(username, password, url);
        } else {
            doLoginOffline(username, password);
        }
    }

    public boolean validate(String username, String password, String url) {
        if (!userService.isNameValid(username)) {
            getView().showUserNameInvalid();
            return false;
        }

        if (!userService.isPasswordValid(password)) {
            getView().showPasswordInvalid();
            return false;
        }

        if (!userService.isUrlValid(url)) {
            getView().showUrlInvalid();
            return false;
        }

        return true;
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

    private void doLoginOnline(final String username,
                             final String password, final String url) {
        loginService.doLoginOnline(username, password, url, PrimeroConfiguration.getAndroidId(), new LoginService.LoginCallback() {
            @Override
            public void onLoginSuccessful(String cookie, User user) {
                if (isViewAttached()) {
                    PrimeroConfiguration.setCookie(cookie);
                    PrimeroConfiguration.setCurrentUser(user);
                    userService.saveOrUpdateUser(user);

                    sendLoadFormEvent(cookie);

                    showLoadingIndicator(false);
                    getView().goToLoginSuccessScreen();
                }
            }

            @Override
            public void onLoginFailed(Throwable error) {
                if (isViewAttached()) {
                    showNetworkErrorMessage(error);
                    showLoadingIndicator(false);
                    doLoginOffline(username, password);
                }
            }

            @Override
            public void onLoginError(int code) {
                getView().showErrorByResId(HttpStatusCodeHandler
                        .getHttpStatusMessage(code));
                doLoginOffline(username, password);

                Log.d(TAG, "login failed");
            }
        });
    }

    private void sendLoadFormEvent(String cookie) {
        EventBus.getDefault().postSticky(new LoadGBVIncidentFormEvent(cookie));
        EventBus.getDefault().postSticky(new LoadGBVCaseFormEvent(cookie));
        EventBus.getDefault().postSticky(new LoadCPCaseFormEvent(cookie));
        EventBus.getDefault().postSticky(new LoadTracingFormEvent(cookie));
    }

    private void doLoginOffline(String username, String password) {
        UserService.VerifiedCode verifiedCode = userService.verify(username, password);

        showLoadingIndicator(false);
        getView().showErrorByResId(verifiedCode.getResId());

        if (verifiedCode == UserService.VerifiedCode.OK) {
            PrimeroConfiguration.setCurrentUser(userService.getUser(username));
            getView().goToLoginSuccessScreen();
        }
    }

    private void showLoadingIndicator(boolean active) {
        getView().showLoading(active);
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
