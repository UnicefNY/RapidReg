package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.ToggleableRadioButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleLineRadioViewHolder extends BaseViewHolder<Field> {

    private static final int MAX_HORIZONAL_SIZE = 2;

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.option_group)
    RadioGroup optionGroup;

    private List<String> options;

    private String result;

    public SingleLineRadioViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(final Field field) {
        String labelText = getLabel(field);

        if (isRequired(field)) {
            labelText += " (Required)";
        }
        options = field.getSelectOptions();
        initRadioGroupView(options, isEditable(field));

        labelView.setHint(labelText);
        setEditableBackgroundStyle(isEditable(field));

        if (isSubFormField(field)) {
            if (!TextUtils.isEmpty(getValueForSubForm(field))) {
                setSelectedRadio(getValueForSubForm(field));
            }
        } else {
            if (!TextUtils.isEmpty(itemValues.getAsString(field.getName()))) {
                setSelectedRadio(itemValues.getAsString(field.getName()));
            }
        }
    }

    private void initRadioGroupView(List<String> options, boolean editable) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                options.size());
        optionGroup.setLayoutParams(layoutParams);

        RadioGroup.LayoutParams radioButtonLayoutParams = new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        if (options.size() > MAX_HORIZONAL_SIZE) {
            optionGroup.setOrientation(LinearLayout.VERTICAL);
            radioButtonLayoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        for (int index = 0; index < options.size(); index ++) {
            addRadioButtonToGroup(options.get(index), index, editable, radioButtonLayoutParams);
        }
    }

    private void addRadioButtonToGroup(String option, int index, boolean editable, RadioGroup.LayoutParams radioButtonLayoutParams) {
        ToggleableRadioButton radioButton = new ToggleableRadioButton(context);
        radioButton.setText(option);
        radioButton.setTextColor(context.getResources().getColor(R.color.primero_font_medium));
        disableUneditableField(editable, radioButton);
        optionGroup.addView(radioButton, index, radioButtonLayoutParams);
    }

    @Override
    public void setOnClickListener(final Field field) {
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int index = 0; index < optionGroup.getChildCount(); index ++) {
                    if (checkedId == optionGroup.getChildAt(index).getId()) {
                        result = options.get(index);
                        itemValues.addStringItem(field.getName(), getResult());
                        return;
                    }
                }
                itemValues.addStringItem(field.getName(), null);
            }
        });
    }

    @Override
    protected String getResult() {
        return result;
    }

    @Override
    public void setFieldEditable(boolean editable) {
        disableUneditableField(editable, optionGroup);
    }

    public void setSelectedRadio(String selectedRadio) {
        for (int index = 0; index < optionGroup.getChildCount(); index ++) {
            ToggleableRadioButton radioButton = (ToggleableRadioButton) optionGroup.getChildAt(index);
            if (selectedRadio.equals(radioButton.getText())) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }
    }
}
