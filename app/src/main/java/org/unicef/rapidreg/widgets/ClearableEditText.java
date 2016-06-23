package org.unicef.rapidreg.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.unicef.rapidreg.R;

public class ClearableEditText extends RelativeLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String HINT = "hint";
    private static final String INPUT_TYPE = "inputType";
    private static final String MAX_LENGTH = "maxLength";
    private static final String SINGLE_LINE = "singleLine";
    private static final int INVALID_ID = -1;
    private EditText editView;
    private ImageButton clearView;

    public ClearableEditText(Context context) {
        super(context);
        initViews(null);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    public String getText() {
        return editView.getText().toString();
    }

    private void initViews(AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View container = inflater.inflate(R.layout.clearable_edit_text, this, true);
        initClearView(container);
        initEditView(container, attrs);
    }

    private void initEditView(View container, AttributeSet attrs) {
        editView = (EditText) container.findViewById(R.id.edit_text);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearView.setVisibility(View.VISIBLE);
                } else {
                    clearView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setAttributes(attrs);
    }

    private void initClearView(View container) {
        clearView = (ImageButton) container.findViewById(R.id.clear_text);
        clearView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editView.setText("");
            }
        });
        clearView.setVisibility(View.INVISIBLE);
    }

    private void setAttributes(AttributeSet attrs) {
        if (attrs != null) {
            setHint(attrs);
            setInputType(attrs);
            setMaxLength(attrs);
            setSingleLine(attrs);
        }
    }

    private void setHint(AttributeSet attrs) {
        int hintId = attrs.getAttributeResourceValue(NAMESPACE, HINT, INVALID_ID);
        if (hintId != INVALID_ID) {
            editView.setHint(hintId);
        }
    }

    private void setInputType(AttributeSet attrs) {
        String inputType = attrs.getAttributeValue(NAMESPACE, INPUT_TYPE);

        if (inputType != null) {
            editView.setInputType(Integer.decode(inputType));
        }
    }

    private void setMaxLength(AttributeSet attrs) {
        String maxLength = attrs.getAttributeValue(NAMESPACE, MAX_LENGTH);

        if (maxLength != null) {
            editView.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(Integer.valueOf(maxLength))});
        }
    }

    private void setSingleLine(AttributeSet attrs) {
        String singleLine = attrs.getAttributeValue(NAMESPACE, SINGLE_LINE);

        if (singleLine != null) {
            editView.setSingleLine(Boolean.valueOf(singleLine));
        }
    }
}
