package org.unicef.rapidreg.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import org.unicef.rapidreg.R;

public class BaseProgressDialog extends ProgressDialog {

    Context context;

    public BaseProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(getSpannableString(message));
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(getSpannableString(title));
    }

    @NonNull
    private SpannableString getSpannableString(CharSequence message) {
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new RelativeSizeSpan(context.getResources()
                .getDimension(R.dimen.dialog_text_relativ_size)), 0, spannableString.length(), 0);
        return spannableString;
    }

}
