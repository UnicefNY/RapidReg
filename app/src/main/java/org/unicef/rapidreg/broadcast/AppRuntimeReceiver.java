package org.unicef.rapidreg.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.unicef.rapidreg.AppRuntime;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.login.AccountManager;
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
        switch (action) {
            case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                Log.d(TAG, "Action reason: " + reason);
                switch (reason) {
                    case SYSTEM_DIALOG_REASON_RECENT_APPS:
                    case SYSTEM_DIALOG_REASON_DREAM:
                    case SYSTEM_DIALOG_REASON_HOME_KEY:
                    case SYSTEM_DIALOG_REASON_LOCK:
                    case SYSTEM_DIALOG_REASON_ASSIST:
                        AccountManager.doSignOut();
                        return;
                }
                break;
            case Intent.ACTION_SCREEN_OFF:
                AccountManager.doSignOut();
                break;
        }
    }
}
