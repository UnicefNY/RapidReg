package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextViewHolder extends BaseTextViewHolder {
    public static final String TAG = TextViewHolder.class.getSimpleName();
    private static final int AGE_MAX_LENGTH = 3;

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.value)
    EditText valueView;

    @BindView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;

    @BindView(R.id.form_question)
    TextView formQuestion;

    private InputMethodManager inputMethodManager;

    public TextViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void setValue(Field field) {
        valueView.setTag(field.getName());
        String labelText = getLabel(field);

        if (isRequired(field)) {
            labelText += " (Required)";
        }

        labelView.setHint(labelText);
        formQuestion.setHint(labelText);

        initValueViewData(field);

        disableUneditableField(isEditable(field), valueView);
        setEditableBackgroundStyle(isEditable(field));

        valueView.setError(null);
        valueView.setSingleLine(true);
        valueView.setInputType(InputType.TYPE_CLASS_TEXT);
        if (field.isTextArea()) {
            valueView.setSingleLine(false);
            valueView.setScrollContainer(false);
        } else if (field.isNumericField()) {
            valueView.setInputType(InputType.TYPE_CLASS_NUMBER);
            valueView.setRawInputType(Configuration.KEYBOARD_12KEY);
        }

        if (TextUtils.isEmpty(valueView.getText())) {
            viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
        } else {
            viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
        }

        initValueViewStatus();
    }

    @Override
    public void setOnClickListener(final Field field) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
                valueView.setFocusableInTouchMode(true);
                valueView.setFocusable(true);
                valueView.requestFocus();
                inputMethodManager.showSoftInput(valueView, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        valueView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == valueView.getId()) {
                    if (!hasFocus) {
                        if (TextUtils.isEmpty(valueView.getText())) {
                            viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
                        } else {
                            viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
                        }
                        itemView.setClickable(true);
                    } else {
                        itemView.setClickable(false);
                    }
                }
            }
        });

        valueView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String verifyResult = verifyValue(String.valueOf(s), field);
                valueView.setError(verifyResult);
                valueView.postInvalidate();
                saveValues(field);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private String verifyValue(String value, Field field) {
        if (field.isNumericField()) {
            if (field.getName().contains(Field.ValidationKeywords.AGE_KEY)) {
                if (!isAgeValid(value)) {
                    return "Age should between 0 - 130";
                }
            }
        }
        return null;
    }

    private boolean isAgeValid(String ageContent) {
        if (TextUtils.isEmpty(ageContent)) {
            return true;
        }
        if (ageContent.length() > AGE_MAX_LENGTH) {
            return false;
        }
        int age = Integer.valueOf(ageContent);
        if (age < RecordService.AGE_MIN || age > RecordService.AGE_MAX) {
            return false;
        }
        return true;
    }

    @Override
    public void setFieldEditable(boolean editable) {
        disableUneditableField(editable, valueView);
    }

    @Override
    public void setFieldClickable(boolean clickable) {

    }

    @Override
    protected String getResult() {
        return valueView.getText().toString();
    }

    @Override
    protected TextView getValueView() {
        return valueView;
    }

}
