package org.unicef.rapidreg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.login.LoginActivity;

public class IntentSender {
    public static final String KEY_LOGIN_USER = "login_user";

    public void showCasesActivity(Activity context, String username) {
        Intent intent = new Intent(context, CaseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LOGIN_USER, username);
        intent.putExtras(bundle);
        context.startActivity(intent);
        context.finish();
    }

    public void showLoginActivity(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        context.finish();
    }
}
