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

import javax.inject.Inject;

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

    public void doLogin(String username, String password, String url, String imei) {
        if (!validate(username, password, url)) {
            return;
        }

        PrimeroConfiguration.setApiBaseUrl(url.endsWith("/") ? url : String.format("%s/", url));
        try {
            getView().showLoading(true);
            if (loginService.isOnline()) {
                doLoginOnline(username, password, url, imei);
            } else {
                doLoginOffline(username, password);
            }
        } catch (Exception e) {
            getView().showLoginErrorByToast(e.getMessage());
        }
    }

    public boolean validate(String username, String password, String url) {
        boolean isValid = true;
        if (!loginService.isUsernameValid(username)) {
            getView().showUserNameInvalid();
            isValid = false;
        }
        if (!loginService.isUsernameValid(password)) {
            getView().showPasswordInvalid();
            isValid = false;
        }

        if (!loginService.isUrlValid(url)) {
            getView().showUrlInvalid();
            isValid = false;
        }

        return isValid;
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
                               final String password,
                               final String url,
                               String imei) {
        loginService.loginOnline(username, password, url, imei, new LoginServiceImpl.LoginCallback() {
            @Override
            public void onSuccessful(String cookie, User user) {
                if (isViewAttached()) {
                    PrimeroConfiguration.setCookie(cookie);
                    PrimeroConfiguration.setCurrentUser(user);

                    sendLoadFormEvent(user.getRoleType(), cookie);

                    getView().showLoading(false);
                    getView().showLoginSuccessful();
                    getView().goToLoginSuccessScreen();
                }
            }

            @Override
            public void onFailed(Throwable error) {
                if (isViewAttached()) {
                    getView().showError(error, false);
                    getView().showLoading(false);
                    doLoginOffline(username, password);
                }
            }

            @Override
            public void onError(int code) {
                getView().showLoginErrorByResId(HttpStatusCodeHandler
                        .getHttpStatusMessage(code));

                getView().showLoading(false);
                Log.d(TAG, "login failed");
            }
        });
    }

    private void sendLoadFormEvent(User.Role roleType, String cookie) {
        switch (roleType) {
            case CP:
                EventBus.getDefault().postSticky(new LoadCPCaseFormEvent(cookie));
                EventBus.getDefault().postSticky(new LoadTracingFormEvent(cookie));
                break;
            case GBV:
                EventBus.getDefault().postSticky(new LoadGBVIncidentFormEvent(cookie));
                EventBus.getDefault().postSticky(new LoadGBVCaseFormEvent(cookie));
                break;
            default:
                break;
        }
    }

    private void doLoginOffline(String username, String password) {
        loginService.loginOffline(username, password, new LoginService.LoginCallback() {
            @Override
            public void onSuccessful(String cookie, User user) {
                PrimeroConfiguration.setCurrentUser(user);
                getView().showLoading(false);
                getView().showLoginSuccessful();
                getView().goToLoginSuccessScreen();
            }

            @Override
            public void onFailed(Throwable error) {
                getView().showLoading(false);
                getView().showLoginErrorByToast(error.getMessage());
            }

            @Override
            public void onError(int code) {
                getView().showLoading(false);
                getView().showLoginErrorByResId(code);
            }
        });
    }

    public boolean isOnline() {
        return loginService.isOnline();
    }
}
