package org.unicef.rapidreg.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import org.unicef.rapidreg.R;

public class BaseAlertDialog extends AlertDialog {

    protected BaseAlertDialog(Context context) {
        super(context);
    }

    protected BaseAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected BaseAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder extends AlertDialog.Builder{

        private Context context;

        public Builder(Context context) {
            super(context);
            this.context = context;
        }

        public Builder(Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        public AlertDialog.Builder setTitle(int titleId) {
            return setTitle(context.getResources().getString(titleId));
        }

        @Override
        public AlertDialog.Builder setTitle(CharSequence title) {
            return super.setTitle(getSpannableString(title));
        }

        @Override
        public AlertDialog.Builder setMessage(int messageId) {
            return setMessage(context.getResources().getString(messageId));
        }

        @Override
        public AlertDialog.Builder setMessage(CharSequence message) {
            return super.setMessage(getSpannableString(message));
        }

        @Override
        public AlertDialog.Builder setPositiveButton(int textId, OnClickListener listener) {
            return setPositiveButton(context.getResources().getString(textId), listener);
        }

        @Override
        public AlertDialog.Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            return super.setPositiveButton(getSpannableString(text), listener);
        }

        @Override
        public AlertDialog.Builder setNegativeButton(int textId, OnClickListener listener) {
            return setNegativeButton(context.getResources().getString(textId), listener);
        }

        @Override
        public AlertDialog.Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            return super.setNegativeButton(getSpannableString(text), listener);
        }

        @Override
        public AlertDialog.Builder setNeutralButton(int textId, OnClickListener listener) {
            return setNeutralButton(context.getResources().getString(textId), listener);
        }

        @Override
        public AlertDialog.Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            return super.setNeutralButton(getSpannableString(text), listener);
        }

        @NonNull
        private SpannableString getSpannableString(CharSequence message) {
            SpannableString spannableString = new SpannableString(message);
            spannableString.setSpan(new RelativeSizeSpan(context.getResources()
                    .getDimension(R.dimen.dialog_text_relativ_size)), 0, spannableString.length(), 0);
            return spannableString;
        }
    }
}
