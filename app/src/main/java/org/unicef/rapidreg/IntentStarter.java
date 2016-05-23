package org.unicef.rapidreg;

import android.app.Activity;
import android.content.Intent;

import org.unicef.rapidreg.cases.CasesActivity;

public class IntentStarter {

    public void showCasesActivity(Activity context) {
        Intent intent = new Intent(context, CasesActivity.class);
        context.startActivity(intent);
        context.finish();
    }
}
