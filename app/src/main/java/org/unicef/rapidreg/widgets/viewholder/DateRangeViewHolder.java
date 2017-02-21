package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateRangeViewHolder extends BaseViewHolder<Field> {

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.date_type_option_group)
    RadioGroup dateTypeOptionGroup;

    @BindView(R.id.date_radio_button)
    RadioButton dateRadioButton;

    @BindView(R.id.date_range_radio_button)
    RadioButton dateRangeRadioButton;

    @BindView(R.id.date_layout)
    LinearLayout dateLayout;

    @BindView(R.id.date_value)
    TextView dateValView;

    @BindView(R.id.date_range_layout)
    LinearLayout dateRangeLayout;

    @BindView(R.id.date_from_value)
    TextView dateFromValView;

    @BindView(R.id.date_to_value)
    TextView dateToValView;

    public DateRangeViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(Field field) {
        String labelText = getLabel(field);

        if (isRequired(field)) {
            labelText += " (Required)";
        }
        labelView.setHint(labelText);
        setEditableBackgroundStyle(isEditable(field));
    }

    @Override
    public void setOnClickListener(Field field) {
        dateTypeOptionGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (dateRadioButton.isChecked()) {
                dateLayout.setVisibility(View.VISIBLE);
                dateRangeLayout.setVisibility(View.GONE);
            }
            if (dateRangeRadioButton.isChecked()) {
                dateRangeLayout.setVisibility(View.VISIBLE);
                dateLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setFieldEditable(boolean editable) {
        disableUneditableField(editable, dateRadioButton);
        disableUneditableField(editable, dateRangeRadioButton);
    }

    @Override
    public void setFieldClickable(boolean clickable) {

    }
}
