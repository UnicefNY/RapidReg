package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TickBoxViewHolder extends BaseViewHolder<CaseField> {

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.value)
    CheckBox valueView;

    public TickBoxViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(CaseField field) {
        labelView.setText(getLabel(field));
        disableUneditableField(isEditable(field), valueView);
        setEditableBackgroundStyle(isEditable(field));

        if (isSubformField(field)) {
            valueView.setChecked(Boolean.valueOf(getValue(field)));
        } else {
            valueView.setChecked(Boolean.valueOf(CaseFieldValueCache.get(getLabel(field))));
        }
    }

    @Override
    public void setOnClickListener(final CaseField field) {
        valueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSubformField(field)) {
                    SubformCache.put(field.getParent(), getValues(field, getResult()));
                } else {
                    CaseFieldValueCache.put(getLabel(field), getResult());
                }
            }
        });
    }

    protected String getResult() {
        return String.valueOf(valueView.isChecked());
    }

    @Override
    public void setFieldEditable(boolean editable) {
        disableUneditableField(editable, valueView);
    }

    private List<Map<String, String>> getValues(CaseField field, String isChecked) {
        String language = Locale.getDefault().getLanguage();
        List<Map<String, String>> values = SubformCache.get(field.getParent()) == null ?
                new ArrayList<Map<String, String>>() : SubformCache.get(field.getParent());

        Map<String, String> value;
        try {
            value = values.get(field.getIndex());
            value.put(field.getDisplayName().get(language), isChecked);
            values.set(field.getIndex(), value);
        } catch (IndexOutOfBoundsException e) {
            value = new HashMap<>();
            value.put(field.getDisplayName().get(language), isChecked);
            values.add(field.getIndex(), value);
        }

        return values;
    }
}
