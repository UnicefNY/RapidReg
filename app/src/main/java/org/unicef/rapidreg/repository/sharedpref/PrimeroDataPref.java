package org.unicef.rapidreg.repository.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.sync.SyncStatisticData;
import org.unicef.rapidreg.utils.DesAlgorithm;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.HashMap;

public class PrimeroDataPref {
    private static final String TAG = PrimeroDataPref.class.getSimpleName();

    private final HashMap<String, SharedPreferences> userDataSharedPreferences;
    private final Context context;

    private SharedPreferences.Editor editor;

    private static final String PREF_SYNC_KEY = "sync_data";
    private static final String PREF_KEY_SERVER_URL = "__server_url";
    private static final String PREF_NAME_SERVER_URL = "__last_login_server_url";

    public PrimeroDataPref(Context context) {
        userDataSharedPreferences = new HashMap<>();
        this.context = context;
    }

    public void storeLastLoginServerUrl(String serverUrl) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME_SERVER_URL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_KEY_SERVER_URL, serverUrl);
        editor.apply();
    }

    public String loadLastLoginServerUrl() {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME_SERVER_URL, Context.MODE_PRIVATE);
        if (preferences == null) {
            return "";
        }
        return preferences.getString(PREF_KEY_SERVER_URL, "");
    }

    public void storeSyncData(SyncStatisticData syncStatisticData) {
        SharedPreferences sharedPreferences = getCurrentUserSharedPref();
        if (sharedPreferences == null) {
            return;
        }
        editor = sharedPreferences.edit();
        editor.putString(PREF_SYNC_KEY, new Gson().toJson(syncStatisticData));
        editor.apply();
    }

    public SyncStatisticData loadSyncData() {
        SharedPreferences sharedPreferences = getCurrentUserSharedPref();
        if (sharedPreferences == null) {
            return new SyncStatisticData();
        }

        String syncDataJson = sharedPreferences.getString(PREF_SYNC_KEY, "");
        if (TextUtils.isEmpty(syncDataJson)) {
            return new SyncStatisticData();
        }

        return new Gson().fromJson(syncDataJson, SyncStatisticData.class);
    }

    public SharedPreferences getCurrentUserSharedPref() {
        User user = PrimeroAppConfiguration.getCurrentUser();
        if (user == null) {
            return null;
        }
        try {
            String prefName = DesAlgorithm.getInstance().desEncrypt(user.getUserInfo());
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
            return sharedPreferences;
        } catch (Exception e) {
            return null;
        }
    }
}
