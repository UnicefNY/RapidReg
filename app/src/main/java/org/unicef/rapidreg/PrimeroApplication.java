package org.unicef.rapidreg;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.unicef.rapidreg.model.User;

public class PrimeroApplication extends Application{

    public static final String SHARED_PREFERENCES_FILE = "RAPIDREG_PREFERENCES";
    public static final String CURRENT_USER_PREF = "CURRENT_USER";

    private Gson gson = new Gson();
    private User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public SharedPreferences getSharedPreferences() {
        SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return sharedPreferences;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        String jsonForUser = (currentUser == null) ? null : gson.toJson(currentUser);
        if (TextUtils.isEmpty(jsonForUser)) {
            getSharedPreferences().edit().remove(CURRENT_USER_PREF).commit();
        } else {
            getSharedPreferences().edit().putString(CURRENT_USER_PREF, jsonForUser).commit();
        }
        this.currentUser = getUserFromSharedPreference();
    }

    public User getUserFromSharedPreference() {
        String jsonForCurrentUser = getSharedPreferences().getString(CURRENT_USER_PREF, null);
        return jsonForCurrentUser == null ? null : gson.fromJson(jsonForCurrentUser, User.class);
    }
    // TODO: need to realise get in progress Sychronization tasks
    public Object getSyncTask() {
        return null;
    }
}
