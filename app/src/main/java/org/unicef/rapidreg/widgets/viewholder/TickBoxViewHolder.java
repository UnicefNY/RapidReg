package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TickBoxViewHolder extends BaseViewHolder<Field> {

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.value)
    CheckBox valueView;

    public TickBoxViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(Field field) {
        labelView.setText(getLabel(field));
        disableUneditableField(isEditable(field), valueView);
        setEditableBackgroundStyle(isEditable(field));

        if (isSubFormField(field)) {
            valueView.setChecked(Boolean.valueOf(getValueForSubForm(field)));
        } else {
            valueView.setChecked(itemValues.getAsBoolean(field.getName()));
        }
    }

    @Override
    public void setOnClickListener(final Field field) {
        valueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemValues.addBooleanItem(field.getName(), getResult());
            }
        });
    }

    protected Boolean getResult() {
        return valueView.isChecked();
    }

    @Override
    public void setFieldEditable(boolean editable) {
        disableUneditableField(editable, valueView);
    }

    @Override
    public void setFieldClickable(boolean clickable) {

    }
}
