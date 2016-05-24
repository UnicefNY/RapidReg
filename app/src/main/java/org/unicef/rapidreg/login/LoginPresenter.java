package org.unicef.rapidreg.login;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.IntentStarter;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.NeedCacheForOfflineEvent;
import org.unicef.rapidreg.event.NeedDoLoginOffLineEvent;
import org.unicef.rapidreg.event.NeedGoToLoginSuccessScreenEvent;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.network.HttpStatusCodeHandler;
import org.unicef.rapidreg.network.NetworkServiceGenerator;
import org.unicef.rapidreg.network.NetworkStatusManager;
import org.unicef.rapidreg.network.PrimeroClient;
import org.unicef.rapidreg.utils.ValidatesUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public static final String TAG = LoginPresenter.class.getSimpleName();

    private PrimeroApplication primeroApplication;
    private PrimeroClient client;
    private ConnectivityManager connectivityManager;
    private Gson gson;
    private Context context;
    private IntentStarter intentStarter;

    public void doLogin(Context context, String username, String password, String url) {
        if (!validate(context, username, password, url)) {
            return;
        }
        if (isViewAttached()) {
            initContext(context, url);
            showLoadingIndicator(true);
            if (NetworkStatusManager.isOnline(connectivityManager)) {
                doLoginOnline(context, username, password, url);
            } else {
                doLoginOffline(context, username, password);
            }
        }
    }

    private boolean validate(Context context, String username, String password, String url) {
        boolean valid = true;
        if (TextUtils.isEmpty(username) || username.length() > 254 || ValidatesUtils.containsSpecialCharacter(username)) {
            getView().getUsernameView().setError(context.getResources().getString(R.string.login_username_invalid_text));
            valid = false;
        } else {
            getView().getUsernameView().setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            getView().getPasswordView().setError(context.getResources().getString(R.string.login_password_invalid_text));
            valid = false;
        } else {
            getView().getPasswordView().setError(null);
        }

        if (TextUtils.isEmpty(url) || !Patterns.WEB_URL.matcher(url).matches()) {
            getView().getUrlView().setError(context.getResources().getString(R.string.login_url_invalid_text));
            valid = false;
        } else {
            getView().getUrlView().setError(null);
        }
        return valid;
    }


    private void initContext(Context context, String url) {
        this.context = context;
        intentStarter = new IntentStarter();
        primeroApplication = (PrimeroApplication) context.getApplicationContext();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        gson = new Gson();
        try {
            NetworkServiceGenerator.changeApiBaseUrl(url);
            client = NetworkServiceGenerator.createService(context, PrimeroClient.class);
        } catch (Exception e) {
            showLoginResultMessage(e.getMessage());
            return;
        }
    }

    private void doLoginOnline(final Context context, final String username, final String password, final String url) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Call<LoginResponse> call = client.login(new LoginRequestBody(
                username,
                password,
                telephonyManager.getLine1Number(),
                android_id));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (isViewAttached()) {
                    showLoadingIndicator(false);
                    if (response.isSuccessful()) {
                        User user = new User(username, password, true, url);
                        user.setDbKey(response.body().getDb_key());
                        user.setOrganisation(response.body().getOrganization());
                        user.setLanguage(response.body().getLanguage());
                        user.setVerified(response.body().getVerified());
                        notifyEvent(new NeedCacheForOfflineEvent(user));
                        notifyEvent(new NeedGoToLoginSuccessScreenEvent());
                        showLoginResultMessage(HttpStatusCodeHandler.LOGIN_SUCCESS_MESSAGE);
                    } else {
                        showLoginResultMessage(HttpStatusCodeHandler.getHttpStatusMessage(response.code()));
                        notifyEvent(new NeedDoLoginOffLineEvent(context, username, password));
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

    //TODO: refactor doLoginOFFLine using encry password interface
    private void doLoginOffline(Context context, String username, String password) {
        if (!primeroApplication.getSharedPreferences().contains(username)) {
            showLoadingIndicator(false);
            showLoginResultMessage(context.getResources().getString(R.string.login_offline_no_user_text));
        } else {
            User user = loadOffLine(username, password, context);
            if (user != null) {

                cacheForOffline(user);
                showLoadingIndicator(false);
                showLoginResultMessage(context.getResources().getString(R.string.login_offline_success_text));
                goToLoginSuccessScreen();
            }
        }
    }

    private User loadOffLine(String username, String password, Context context) {
        String jsonForUser = primeroApplication.getSharedPreferences().getString(username, null);
        User user = gson.fromJson(jsonForUser, User.class);
        if (user.getPassword().equals(password)) {
            return user;
        }
        showLoadingIndicator(false);
        showLoginResultMessage(context.getResources().getString(R.string.login_failed_text));
        return null;
    }
    //End TODO


    private void cacheForOffline(@NonNull User user) {
        String jsonForUser = gson.toJson(user);
        primeroApplication.getSharedPreferences().edit().putString(user.getUserName(), jsonForUser).commit();
        primeroApplication.setCurrentUser(user);
    }

    private void goToLoginSuccessScreen() {
        intentStarter.showCasesActivity((Activity) context);
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

    @Override
    public void attachView(LoginView view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedDoLoginOffLineEvent(NeedDoLoginOffLineEvent event) {
        doLoginOffline(event.context, event.username, event.password);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedCacheForOfflineEvent(NeedCacheForOfflineEvent event) {
        cacheForOffline(event.user);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedGoToLoginSuccessScreenEvent(NeedGoToLoginSuccessScreenEvent event) {
        goToLoginSuccessScreen();
    }

}
