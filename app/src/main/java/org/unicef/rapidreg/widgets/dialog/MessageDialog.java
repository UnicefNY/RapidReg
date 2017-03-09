package org.unicef.rapidreg.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.unicef.rapidreg.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageDialog extends Dialog {

    @BindView(R.id.dialog_title)
    TextView dialogTitleTV;

    @BindView(R.id.dialog_message)
    TextView dialogMessageTV;

    @BindView(R.id.dialog_custom_view_content)
    FrameLayout dialogCustonViewContent;

    @BindView(R.id.date_picker_content)
    FrameLayout datePickerContent;

    @BindView(R.id.date_picker)
    DatePicker datePicker;

    @BindView(R.id.okButton)
    Button okButton;

    @BindView(R.id.cancelButton)
    Button cancelButton;

    @BindView(R.id.clearButton)
    Button clearButton;

    public MessageDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_with_message);
        ButterKnife.bind(this);

        initDialog();
    }

    private void initDialog() {
        dialogTitleTV.setVisibility(View.GONE);
        okButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        dialogMessageTV.setVisibility(View.GONE);
        dialogCustonViewContent.setVisibility(View.GONE);
        datePickerContent.setVisibility(View.GONE);
        datePicker.setVisibility(View.GONE);
    }

    @Override
    public void setTitle(int titleId) {
        dialogTitleTV.setVisibility(View.VISIBLE);
        dialogTitleTV.setText(titleId);
    }

    public void setMessage(int messageId) {
        dialogMessageTV.setVisibility(View.VISIBLE);
        dialogMessageTV.setText(messageId);
    }

    public void setMessage(String message) {
        dialogMessageTV.setVisibility(View.VISIBLE);
        dialogMessageTV.setText(message);
    }

    public void setMessageColor(int colorId) {
        dialogMessageTV.setTextColor(colorId);
    }

    public void setMessageTextSize(int textSizeId) {
        dialogMessageTV.setTextSize(getContext().getResources().getDimension(textSizeId));
    }

    public void setMessageTextSize(float textSize) {
        dialogMessageTV.setTextSize(textSize);
    }

    public void setCustonView(View view) {
        dialogCustonViewContent.setVisibility(View.VISIBLE);
        dialogCustonViewContent.addView(view);
    }

    public void setPositiveButton(int buttonNameId, View.OnClickListener onClickListener) {
        okButton.setVisibility(View.VISIBLE);
        okButton.setText(buttonNameId);
        okButton.setOnClickListener(onClickListener);
    }

    public void setNegativeButton(int buttonNameId, View.OnClickListener onClickListener) {
        cancelButton.setVisibility(View.VISIBLE);
        cancelButton.setText(buttonNameId);
        cancelButton.setOnClickListener(onClickListener);
    }

    public void setMiddleButton(int buttonNameId, View.OnClickListener onClickListener) {
        clearButton.setVisibility(View.VISIBLE);
        clearButton.setText(buttonNameId);
        clearButton.setOnClickListener(onClickListener);
    }

    public DatePicker getDatePicker() {
        datePickerContent.setVisibility(View.VISIBLE);
        datePicker.setVisibility(View.VISIBLE);
        return datePicker;
    }
}
