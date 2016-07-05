package org.unicef.rapidreg.login;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.raizlabs.android.dbflow.data.Blob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.NeedCacheForOfflineEvent;
import org.unicef.rapidreg.event.NeedDoLoginOffLineEvent;
import org.unicef.rapidreg.event.NeedGoToLoginSuccessScreenEvent;
import org.unicef.rapidreg.event.NeedLoadFormsEvent;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.network.HttpStatusCodeHandler;
import org.unicef.rapidreg.network.NetworkServiceGenerator;
import org.unicef.rapidreg.network.NetworkStatusManager;
import org.unicef.rapidreg.network.PrimeroClient;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.utils.EncryptHelper;

import java.util.List;
import java.util.Locale;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.unicef.rapidreg.service.CaseFormService.FormLoadStateMachine;

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public static final String TAG = LoginPresenter.class.getSimpleName();
    private static final int MAX_LOAD_FORMS_NUM = 3;

    private PrimeroClient client;
    private ConnectivityManager cm;
    private Gson gson;
    private Context context;
    private IntentSender intentSender;

    public String fetchURL() {
        try {
            User user = UserService.getInstance().getAllUsers().get(0);
            return user.getServerUrl();
        } catch (Exception e) {
            Log.w("user login", "No user ever login successfully, so url doesn't exist!");
            return "";
        }
    }

    public void doLogin(Context context, String username, String password, String url) {
        if (!validate(context, username, password, url)) {
            return;
        }
        if (isViewAttached()) {
            initContext(context, url);
            showLoadingIndicator(true);
            if (NetworkStatusManager.isOnline(cm)) {
                doLoginOnline(context, username, password, url);
            } else {
                doLoginOffline(context, username, password);
            }
        }
    }

    public boolean validate(Context context, String username, String password, String url) {
        UserService userService = UserService.getInstance();
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedDoLoginOffLineEvent(NeedDoLoginOffLineEvent event) {
        doLoginOffline(event.getContext(), event.getUsername(), event.getPassword());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNeedCacheForOfflineEvent(NeedCacheForOfflineEvent event) {
        UserService service = UserService.getInstance();
        service.setCurrentUser(event.getUser());
        service.saveOrUpdateUser(event.getUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedGoToLoginSuccessScreenEvent(NeedGoToLoginSuccessScreenEvent event) {
        goToLoginSuccessScreen(event.getUserName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedLoadFormsEvent(final NeedLoadFormsEvent event) {
        final FormLoadStateMachine stateMachine = event.getStateMachine();
        stateMachine.addOnce();
        Log.d(TAG, String.format("this is %s time(s) to load forms", stateMachine.getCurrentNum()));

        Call<CaseFormRoot> call = client.getForm(event.getCookie(),
                Locale.getDefault().getLanguage(), true, "case");

        call.enqueue(new Callback<CaseFormRoot>() {
            @Override
            public void onResponse(Call<CaseFormRoot> call, Response<CaseFormRoot> response) {
                if (response.isSuccessful()) {
                    CaseFormRoot form = response.body();
                    CaseForm caseForm = new CaseForm(new Blob(gson.toJson(form).getBytes()));
                    CaseFormService.getInstance().saveOrUpdateCaseForm(caseForm);

                    EventBus.getDefault().unregister(this);
                    Log.i(TAG, "load form successfully");
                } else {
                    Log.d(TAG, String.format("error code: %s", response.code()));
                    reloadFormsIfNeeded(event.getCookie(), stateMachine);
                }
            }

            @Override
            public void onFailure(Call<CaseFormRoot> call, Throwable t) {
                if (isViewAttached()) {
                    showNetworkErrorMessage(t, false);
                    showLoadingIndicator(false);
                }
                reloadFormsIfNeeded(event.getCookie(), stateMachine);
            }
        });
    }

    private void reloadFormsIfNeeded(String cookie, FormLoadStateMachine stateMachine) {
        if (stateMachine.hasReachMaxRetryNum()) {
            EventBus.getDefault().unregister(this);
            Log.d(TAG, "reach the max retry number, stop trying");
        } else {
            notifyEvent(new NeedLoadFormsEvent(cookie, stateMachine));
        }
    }

    private void initContext(Context context, String url) {
        this.context = context;
        intentSender = new IntentSender();
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        gson = new Gson();
        try {

            NetworkServiceGenerator.getInstance().changeApiBaseUrl(url);
            client = NetworkServiceGenerator.getInstance().createService(context, PrimeroClient.class);
        } catch (Exception e) {
            showLoginResultMessage(e.getMessage());
        }
    }

    private void doLoginOnline(final Context context, final String username,
                               final String password, final String url) {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Call<LoginResponse> call = client.login(new LoginRequestBody(
                username,
                password,
                tm.getLine1Number(),
                androidId));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (isViewAttached()) {
                    showLoadingIndicator(false);
                    if (response.isSuccessful()) {
                        User user = new User(username, EncryptHelper.encrypt(password), true, url);
                        user.setDbKey(response.body().getDb_key());
                        user.setOrganisation(response.body().getOrganization());
                        user.setLanguage(response.body().getLanguage());
                        user.setVerified(response.body().getVerified());
                        notifyEvent(new NeedCacheForOfflineEvent(user));
                        notifyEvent(new NeedGoToLoginSuccessScreenEvent(username));
                        notifyEvent(new NeedLoadFormsEvent(getSessionId(response.headers()),
                                FormLoadStateMachine.getInstance(MAX_LOAD_FORMS_NUM)));
                        showLoginResultMessage(HttpStatusCodeHandler.LOGIN_SUCCESS_MESSAGE);
                        Log.d(TAG, "login successfully");
                    } else {
                        showLoginResultMessage(HttpStatusCodeHandler
                                .getHttpStatusMessage(response.code()));
                        notifyEvent(new NeedDoLoginOffLineEvent(context, username, password));
                        Log.d(TAG, "login failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (isViewAttached()) {
                    showNetworkErrorMessage(t, false);
                    showLoadingIndicator(false);
                    notifyEvent(new NeedDoLoginOffLineEvent(context, username, password));
                }
            }
        });
    }

    private void doLoginOffline(Context context, String username, String password) {
        UserService service = UserService.getInstance();
        UserService.VerifiedCode verifiedCode = service.verify(username, password);

        showLoadingIndicator(false);
        showLoginResultMessage(context.getResources().getString(verifiedCode.getResId()));

        if (verifiedCode == UserService.VerifiedCode.OK) {
            service.setCurrentUser(service.getUser(username));
            goToLoginSuccessScreen(username);
        }
    }

    private void goToLoginSuccessScreen(String username) {
        intentSender.showCasesActivity((Activity) context, username);
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

    private void notifyEvent(Object event) {
        EventBus.getDefault().post(event);
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
