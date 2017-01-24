package org.unicef.rapidreg;

import android.app.Activity;
import android.content.Intent;

import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.incident.IncidentActivity;
import org.unicef.rapidreg.login.LoginActivity;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.sync.SyncActivity;
import org.unicef.rapidreg.tracing.TracingActivity;

public class IntentSender {
    public static final String IS_OPEN_MENU = "is_open_menu";
    public static final String IS_FROM_LOGIN = "is_from_login";

    public void showCasesActivity(Activity context, boolean isOpenMenu, boolean isFromLogin) {
        Intent intent = new Intent(context, CaseActivity.class);
        intent.putExtra(IS_OPEN_MENU, isOpenMenu);
        intent.putExtra(IS_FROM_LOGIN, isFromLogin);
        showActivity(context, null, intent);
    }

    public void showTracingActivity(Activity context, boolean isOpenMenu) {
        Intent intent = new Intent(context, TracingActivity.class);
        intent.putExtra(IS_OPEN_MENU, isOpenMenu);
        showActivity(context, null, intent);
    }

    public void showIncidentActivity(Activity context, boolean isOpenMenu) {
        Intent intent = new Intent(context, IncidentActivity.class);
        intent.putExtra(IS_OPEN_MENU, isOpenMenu);
        showActivity(context, null, intent);
    }

    public void showLoginActivity(Activity context) {
        showActivity(context, LoginActivity.class, null);
    }

    public void showSyncActivity(Activity context, boolean isOpenMenu) {
        Intent intent = new Intent(context, SyncActivity.class);
        intent.putExtra(IS_OPEN_MENU, isOpenMenu);
        showActivity(context, null, intent);
    }

    private void showActivity(Activity context, Class<?> cls, Intent intent) {
        if (intent == null) {
            intent = new Intent(context, cls);
        }
        context.startActivity(intent);
        context.finish();
    }
}
