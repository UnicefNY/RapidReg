package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.VerifyWithoutPopupEditText;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextViewHolder extends BaseTextViewHolder {
    public static final String TAG = TextViewHolder.class.getSimpleName();
    private static final int AGE_MAX_LENGTH = 3;

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.value)
    VerifyWithoutPopupEditText valueView;

    @BindView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;

    @BindView(R.id.form_question)
    TextView formQuestion;

    private InputMethodManager inputMethodManager;

    private LinkedHashMap<String, String> verifyResultMap = new LinkedHashMap<>();

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
        itemView.setOnClickListener(view -> {
            viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
            valueView.setFocusableInTouchMode(true);
            valueView.setFocusable(true);
            valueView.requestFocus();
            inputMethodManager.showSoftInput(valueView, InputMethodManager.SHOW_IMPLICIT);
        });

        valueView.setOnFocusChangeListener((v, hasFocus) -> {
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
        });

        valueView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String verifyResult = verifyValue(String.valueOf(s), field);
                String defaultLanguage = PrimeroAppConfiguration.getDefaultLanguage();
                String fieldVerifyKey = field.getDisplayName().get(defaultLanguage);
                String sectionVerifyKey = field.getSectionName().get(defaultLanguage);
                verifyResultMap = fieldValueVerifyResult.getChildrenAsLinkedHashMap(sectionVerifyKey);
                if (verifyResultMap == null) {
                    verifyResultMap = new LinkedHashMap<>();
                }
                if (!TextUtils.isEmpty(verifyResult)) {
                    verifyResultMap.put(fieldVerifyKey, verifyResult);
                    fieldValueVerifyResult.addLinkedHashMap(sectionVerifyKey, verifyResultMap);
                    valueView.setError(verifyResult);
                } else {
                    if (verifyResultMap.containsKey(fieldVerifyKey)) {
                        verifyResultMap.remove(fieldVerifyKey);
                    }
                    if (verifyResultMap.isEmpty()) {
                        fieldValueVerifyResult.removeItem(sectionVerifyKey);
                    } else {
                        fieldValueVerifyResult.addLinkedHashMap(sectionVerifyKey, verifyResultMap);
                    }
                    valueView.setError(null);
                }
                saveValues(field);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String verifyResult = verifyValue(getResult(), field);
                if (!TextUtils.isEmpty(verifyResult)) {
                    Toast.makeText(context, verifyResult, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String verifyValue(String value, Field field) {
        if (field.isNumericField()) {
            if (field.getName().contains(Field.ValidationKeywords.AGE_KEY)) {
                if (!isAgeValid(value)) {
                    return "Age must be between 0 - 130";
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
