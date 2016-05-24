package org.unicef.rapidreg;

import android.app.Activity;
import android.content.Intent;

import org.unicef.rapidreg.cases.CasesActivity;
import org.unicef.rapidreg.login.LoginActivity;

public class IntentStarter {

    public void showCasesActivity(Activity context) {
        Intent intent = new Intent(context, CasesActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    public void showLoginActivity(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        context.finish();
    }
}
