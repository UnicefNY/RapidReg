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

    @BindView(R.id.first_option)
    RadioButton firstOption;

    @BindView(R.id.second_option)
    RadioButton secondOption;

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
        firstOption.setText(options.get(0));
        secondOption.setText(options.get(1));

        labelView.setHint(labelText);
        disableUneditableField(isEditable(field), firstOption);
        disableUneditableField(isEditable(field), secondOption);
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

    @Override
    public void setOnClickListener(final Field field) {
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                result = (checkedId == firstOption.getId() ? options.get(0) : options.get(1));
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
        disableUneditableField(editable, firstOption);
        disableUneditableField(editable, secondOption);
    }

    public void setSelectedRadio(String selectedRadio) {
        if (selectedRadio.equals(options.get(0))) {
            firstOption.setChecked(true);
            secondOption.setChecked(false);
        } else {
            firstOption.setChecked(false);
            secondOption.setChecked(true);
        }
    }
}
