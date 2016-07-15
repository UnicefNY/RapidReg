package org.unicef.rapidreg;

import android.app.Activity;
import android.content.Intent;

import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.login.LoginActivity;
import org.unicef.rapidreg.sync.SyncActivity;
import org.unicef.rapidreg.tracing.TracingActivity;

public class IntentSender {
    public static final String USER_NAME = "userName";
    public static final String IS_OPEN_MENU = "isOpenMenu";

    public void showCasesActivity(Activity context, String username, boolean isOpenMenu) {
        Intent intent = new Intent(context, CaseActivity.class);
        intent.putExtra(USER_NAME, username);
        intent.putExtra(IS_OPEN_MENU, isOpenMenu);
        showActivity(context, null, intent);
    }

    public void showTracingActivity(Activity context) {
        showActivity(context, TracingActivity.class, null);
    }

    public void showLoginActivity(Activity context) {
        showActivity(context, LoginActivity.class, null);
    }

    public void showSyncActivity(Activity context) {
        showActivity(context, SyncActivity.class, null);
    }

    private void showActivity(Activity context, Class<?> cls, Intent intent) {
        if (intent == null) {
            intent = new Intent(context, cls);
        }
        context.startActivity(intent);
        context.finish();
    }
}
