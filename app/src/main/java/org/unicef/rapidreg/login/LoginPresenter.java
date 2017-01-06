package org.unicef.rapidreg.login;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVIncidentFormEvent;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.network.HttpStatusCodeHandler;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.service.UserService.VerifiedCode;

import javax.inject.Inject;

import static org.unicef.rapidreg.service.LoginService.INVALID_PASSWORD;
import static org.unicef.rapidreg.service.LoginService.INVALID_URL;
import static org.unicef.rapidreg.service.LoginService.INVALID_USERNAME;
import static org.unicef.rapidreg.service.LoginService.VALID;

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginService loginService;

    @Inject
    public LoginPresenter(LoginService loginService) {
        this.loginService = loginService;
    }

    public String fetchURL() {
        return loginService.getServerUrl();
    }

    public void doLogin(String username, String password, String url) {
        if (!validate(username, password, url)) {
            return;
        }

        PrimeroConfiguration.setApiBaseUrl(url.endsWith("/") ? url : String.format("%s/", url));
        try {
            getView().showLoading(true);
            if (loginService.isOnline()) {
                doLoginOnline(username, password, url);
            } else {
                doLoginOffline(username, password);
            }
        } catch (Exception e) {
            getView().showLoginErrorByToast(e.getMessage());
        }
    }

    public boolean validate(String username, String password, String url) {
        int validateCode = loginService.validate(username, password, url);
        switch (validateCode) {
            case VALID: return true;
            case INVALID_USERNAME: getView().showUserNameInvalid(); return false;
            case INVALID_PASSWORD: getView().showPasswordInvalid(); return false;
            case INVALID_URL: getView().showUrlInvalid(); return false;
            default: return false;
        }
    }

    @Override
    public void attachView(LoginView view) {
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        loginService.destroy();
    }

    private void doLoginOnline(final String username,
                             final String password, final String url) {
        loginService.doLoginOnline(username, password, url, PrimeroConfiguration.getAndroidId(), new LoginServiceImpl.LoginCallback() {
            @Override
            public void onLoginSuccessful(String cookie, User user) {
                if (isViewAttached()) {
                    PrimeroConfiguration.setCookie(cookie);
                    PrimeroConfiguration.setCurrentUser(user);

                    sendLoadFormEvent(cookie);

                    getView().showLoading(false);
                    getView().showLoginSuccessful();
                    getView().goToLoginSuccessScreen();
                }
            }

            @Override
            public void onLoginFailed(Throwable error) {
                if (isViewAttached()) {
                    getView().showError(error, false);
                    getView().showLoading(false);
                    doLoginOffline(username, password);
                }
            }

            @Override
            public void onLoginError(int code) {
                getView().showLoginErrorByResId(HttpStatusCodeHandler
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
        loginService.doLoginOffline(username, password, new LoginService.LoginCallback() {
            @Override
            public void onLoginSuccessful(String cookie, User user) {
                PrimeroConfiguration.setCurrentUser(user);
                getView().showLoading(false);
                getView().showLoginSuccessful();
                getView().goToLoginSuccessScreen();
            }

            @Override
            public void onLoginFailed(Throwable error) {
                getView().showLoading(false);
                getView().showLoginErrorByToast(error.getMessage());
            }

            @Override
            public void onLoginError(int code) {
                getView().showLoading(false);
                getView().showLoginErrorByResId(code);
            }
        });
    }

    public boolean isOnline() {
        return loginService.isOnline();
    }
}
