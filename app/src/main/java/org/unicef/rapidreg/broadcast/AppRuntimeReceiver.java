package org.unicef.rapidreg.broadcast;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.unicef.rapidreg.AppRuntime;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.login.AccountManager;
import org.unicef.rapidreg.login.LoginActivity;

import java.util.List;

public class AppRuntimeReceiver extends BroadcastReceiver {
    private static final String TAG = AppRuntimeReceiver.class.getSimpleName();

    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_DREAM = "dream";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    private static final int VERSION_CODE_NOUGAT = 24;

    private Intent previousIntent;

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
                    case SYSTEM_DIALOG_REASON_DREAM:
                    case SYSTEM_DIALOG_REASON_HOME_KEY:
                        dealOnNougat();
                        break;
                    case SYSTEM_DIALOG_REASON_LOCK:
                    case SYSTEM_DIALOG_REASON_ASSIST:
                        AccountManager.doSignOut();
                        break;
                }
                break;
            case Intent.ACTION_SCREEN_OFF:
                AccountManager.doSignOut();
                break;
        }

        previousIntent = intent;
    }

    private void dealOnNougat() {
        if (previousIntent != null && Build.VERSION.SDK_INT == VERSION_CODE_NOUGAT) {
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(previousIntent.getAction()) &&
                    SYSTEM_DIALOG_REASON_RECENT_APPS.equals(previousIntent.getStringExtra(SYSTEM_DIALOG_REASON_KEY))) {
                return;
            }
        }
        AccountManager.doSignOut();
    }
}
