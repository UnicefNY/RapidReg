package org.unicef.rapidreg.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.network.HttpStatusCodeHandler;
import org.unicef.rapidreg.network.NetworkServiceGenerator;
import org.unicef.rapidreg.network.NetworkStatusManager;
import org.unicef.rapidreg.network.PrimeroClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public static final String TAG = LoginPresenter.class.getSimpleName();

    private PrimeroApplication primeroApplication;
    private PrimeroClient client;
    private ConnectivityManager connectivityManager;
    private Gson gson = new Gson();

    public void doLogin(Context context, String username, String password, String url){
        primeroApplication = (PrimeroApplication) context.getApplicationContext();
        try {
            client = NetworkServiceGenerator.createService(context, PrimeroClient.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isViewAttached()) {
            getView().showLoading(true);
            if (NetworkStatusManager.isOnline(
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))) {
                doLoginOnline(context, username, password, url);
            } else {
                doLoginOffline(context, username, password);
            }
        }
    }

    private void doLoginOnline(final Context context, final String username, final String password, final String url) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        PrimeroClient client;
        try {
            client = NetworkServiceGenerator.createService(context, PrimeroClient.class);
        } catch (Exception e) {
            getView().showLoginResult(e.getMessage());
            Log.e(TAG, e.getMessage());
            return;
        }

        Call<LoginResponse> call =
                client.login(new LoginRequestBody(username, password,
                        telephonyManager.getLine1Number(), android_id));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (isViewAttached()) {
                    getView().showLoading(false);
                    if (response.isSuccessful()) {
                        User user = new User(username, password, true, url);
                        user.setDbKey(response.body().getDb_key());
                        user.setOrganisation(response.body().getOrganization());
                        user.setLanguage(response.body().getLanguage());
                        user.setVerified(response.body().getVerified());
                        EventBus.getDefault().post(new NeedCacheForOfflineEvent(user));
                        getView().showLoginResult(HttpStatusCodeHandler.LOGIN_SUCCESS_MESSAGE);
                    } else {
                        getView().showLoginResult(HttpStatusCodeHandler
                                .getHttpStatusMessage(response.code()));
                        EventBus.getDefault().post(new NeedDoLoginOffLineEvent(context, username, password));
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (isViewAttached()) {
                    getView().showError(t, false);
                    getView().showLoading(false);
                    EventBus.getDefault().post(new NeedDoLoginOffLineEvent(context, username, password));
                }
            }
        });
    }

    private void doLoginOffline(Context context, String username, String password) {
        if (!primeroApplication.getSharedPreferences().contains(username)) {
            getView().showLoading(false);
            getView().showLoginResult(
                    context.getResources().getString(R.string.login_offline_no_user_text));
        } else {
            String jsonForUser = primeroApplication.getSharedPreferences().getString(username, null);
            User user = gson.fromJson(jsonForUser, User.class);
            cacheForOffline(user);
            getView().showLoading(false);
            getView().showLoginResult(
                    context.getResources().getString(R.string.login_offline_success_text));
        }
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

    private class NeedCacheForOfflineEvent {
        public User user;

        public NeedCacheForOfflineEvent(User user) {
            this.user = user;
        }
    }

    private class NeedDoLoginOffLineEvent {
        public Context context;
        public String username;
        public String password;

        public NeedDoLoginOffLineEvent(Context context, String username, String password) {
            this.context = context;
            this.username = username;
            this.password = password;
        }
    }

    private void cacheForOffline(@NonNull User user) {
        String jsonForUser = gson.toJson(user);
        primeroApplication.getSharedPreferences().edit().putString(user.getUserName(), jsonForUser).commit();
        primeroApplication.setCurrentUser(user);
    }
}
