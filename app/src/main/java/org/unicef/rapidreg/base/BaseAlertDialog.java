package org.unicef.rapidreg.base;

import android.app.AlertDialog;
import android.content.Context;

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

    public static class Builder extends AlertDialog.Builder {

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
            return super.setTitle(title);
        }

        @Override
        public AlertDialog.Builder setMessage(int messageId) {
            return setMessage(context.getResources().getString(messageId));
        }

        @Override
        public AlertDialog.Builder setMessage(CharSequence message) {
            return super.setMessage(message);
        }

        @Override
        public AlertDialog.Builder setPositiveButton(int textId, OnClickListener listener) {
            return setPositiveButton(context.getResources().getString(textId), listener);
        }

        @Override
        public AlertDialog.Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            return super.setPositiveButton(text, listener);
        }

        @Override
        public AlertDialog.Builder setNegativeButton(int textId, OnClickListener listener) {
            return setNegativeButton(context.getResources().getString(textId), listener);
        }

        @Override
        public AlertDialog.Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            return super.setNegativeButton(text, listener);
        }

        @Override
        public AlertDialog.Builder setNeutralButton(int textId, OnClickListener listener) {
            return setNeutralButton(context.getResources().getString(textId), listener);
        }

        @Override
        public AlertDialog.Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            return super.setNeutralButton(text, listener);
        }

    }
}
