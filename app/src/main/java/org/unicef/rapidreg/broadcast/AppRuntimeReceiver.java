package org.unicef.rapidreg.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.unicef.rapidreg.AppRuntime;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.login.LoginActivity;

public class AppRuntimeReceiver extends BroadcastReceiver {
    private static final String TAG = AppRuntimeReceiver.class.getSimpleName();

    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_DREAM = "dream";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    public AppRuntimeReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

            Log.d(TAG, "Action reason: " + reason);
            switch (reason) {
                case SYSTEM_DIALOG_REASON_RECENT_APPS:
                    //TODO it's unstable, need to discuess whether logout
                    return;
                case SYSTEM_DIALOG_REASON_DREAM:
                    //TODO it's not sensitive, need to update
                    return;
                case SYSTEM_DIALOG_REASON_HOME_KEY:
                case SYSTEM_DIALOG_REASON_LOCK:
                case SYSTEM_DIALOG_REASON_ASSIST:
                    PrimeroAppConfiguration.setCurrentUser(null);
                    PrimeroApplication.getAppRuntime().unbindTemplateCaseService();
                    PrimeroApplication.getAppRuntime().unregisterAppRuntimeReceiver();

                    Context appContext = PrimeroApplication.getAppContext();
                    Intent resetIntent = new Intent(appContext, LoginActivity.class);
                    resetIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    appContext.startActivity(resetIntent);
                    return;
            }
        }
    }
}
