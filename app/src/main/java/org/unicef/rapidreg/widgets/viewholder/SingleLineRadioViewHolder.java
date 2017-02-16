package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleLineRadioViewHolder extends BaseViewHolder<Field> {

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.option_group)
    RadioGroup optionGroup;

    @BindView(R.id.first_radio_button_text_view)
    TextView firstRadioButtonTV;

    @BindView(R.id.second_radio_button_text_view)
    TextView secondRadioButtonTV;

    @BindView(R.id.first_radio_button)
    RadioButton firstRadioButton;

    @BindView(R.id.second_radio_button)
    RadioButton secondRadioButton;

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
        firstRadioButtonTV.setText((options.get(0)));
        secondRadioButtonTV.setText((options.get(1)));
    }

    @Override
    public void setOnClickListener(final Field field) {
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                result = null;
                if (firstRadioButton.isChecked()) {
                    result = options.get(0);
                }
                if (secondRadioButton.isChecked()) {
                    result = options.get(1);
                }
                itemValues.addStringItem(field.getName(), getResult());
            }
        });
    }

    @Override
    protected String getResult() {
        return result;
    }

    @Override
    public void setFieldEditable(boolean editable) {
        disableUneditableField(editable, firstRadioButton);
        disableUneditableField(editable, secondRadioButton);
    }

    @Override
    public void setFieldClickable(boolean clickable) {

    }

    public void setSelectedRadio(String selectedRadio) {
        if (selectedRadio.equals(firstRadioButtonTV.getText())) {
            firstRadioButton.setChecked(true);
        } else {
            firstRadioButton.setChecked(false);
        }
        if (selectedRadio.equals(secondRadioButtonTV.getText())) {
            secondRadioButton.setChecked(true);
        } else {
            secondRadioButton.setChecked(false);
        }
    }
}
