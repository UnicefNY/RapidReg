package org.unicef.rapidreg;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.android.dbflow.structure.database.OpenHelper;

import org.unicef.rapidreg.db.PrimeroDB;
import org.unicef.rapidreg.db.SQLCipherHelperImpl;
import org.unicef.rapidreg.model.User;

public class PrimeroApplication extends Application {

    public static final String SHARED_PREFERENCES_FILE = "RAPIDREG_PREFERENCES";
    public static final String CURRENT_USER_PREF = "CURRENT_USER";
    public static final String FORM_SECTIONS_PREF = "FORM_SECTION";

    private static Context context;

    private Gson gson = new Gson();
    private User currentUser;

    public static Context getAppContext() {
        return context;
    }

    public static boolean isDebugMode() {
        String pkgName = context.getPackageName();
        return (pkgName != null && pkgName.endsWith(".debug"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initDB();
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
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

    public void saveFormSections(String formSectionJson) {
        getSharedPreferences().edit().putString(FORM_SECTIONS_PREF, formSectionJson).commit();
    }

    private void initDB() {
        FlowManager.init(new FlowConfig.Builder(this)
                .addDatabaseConfig(new DatabaseConfig.Builder(PrimeroDB.class)
                        .openHelper(new DatabaseConfig.OpenHelperCreator() {
                            @Override
                            public OpenHelper createHelper(DatabaseDefinition databaseDefinition,
                                                           DatabaseHelperListener helperListener) {
                                return new SQLCipherHelperImpl(databaseDefinition, helperListener);
                            }
                        }).build())
                .build());
    }
}
