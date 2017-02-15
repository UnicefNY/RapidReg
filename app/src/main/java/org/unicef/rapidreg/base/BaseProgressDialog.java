package org.unicef.rapidreg.base;

import android.app.ProgressDialog;
import android.content.Context;

public class BaseProgressDialog extends ProgressDialog {

    Context context;

    public BaseProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }
}
