package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.content.res.Configuration;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextViewHolder extends BaseTextViewHolder {

    public static final String TAG = TextViewHolder.class.getSimpleName();

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.value)
    EditText valueView;

    @BindView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;

    @BindView(R.id.form_question)
    TextView formQuestion;

    private InputMethodManager inputMethodManager;

    public TextViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void setValue(CaseField field) {
        String labelText = getLabel(field);

        if (isRequired(field)) {
            labelText += " (Required)";
        }

        labelView.setHint(labelText);
        formQuestion.setHint(labelText);
        if (isSubformField(field)) {
            valueView.setText(getValue(field));
        } else {
            valueView.setText(CaseFieldValueCache.get(getLabel(field)));
        }
        if (TextUtils.isEmpty(valueView.getText())) {
            viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
        } else {
            viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
        }

        disableUneditableField(isEditable(field), valueView);
        setEditableBackgroundStyle(isEditable(field));

        if (field.isNumericField()) {
            valueView.setInputType(InputType.TYPE_CLASS_NUMBER);
            valueView.setRawInputType(Configuration.KEYBOARD_12KEY);
        }

    }

    @Override
    public void setOnClickListener(final CaseField field) {
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
                            saveValues(field);
                        }
                        itemView.setClickable(true);
                    } else {
                        itemView.setClickable(false);
                    }
                }
            }
        });
    }

    @Override
    protected String getResult() {
        return valueView.getText().toString();
    }

    @Override
    public void setFieldEditable(boolean editable) {
        disableUneditableField(editable, valueView);
    }

    private void saveValues(final CaseField field) {
        String language = Locale.getDefault().getLanguage();

        if (TextUtils.isEmpty(valueView.getText())) {
            return;
        }

        if (isSubformField(field)) {
            SubformCache.put(field.getParent(), getValues(field));
        } else {
            CaseFieldValueCache.put(field.getDisplayName().get(language),
                    valueView.getText().toString());
        }
    }
}
