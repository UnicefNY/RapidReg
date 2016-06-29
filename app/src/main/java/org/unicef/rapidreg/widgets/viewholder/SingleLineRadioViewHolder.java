package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleLineRadioViewHolder extends BaseViewHolder<CaseField> {

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.option_group)
    RadioGroup optionGroup;

    @BindView(R.id.first_option)
    RadioButton firstOption;

    @BindView(R.id.second_option)
    RadioButton secondOption;

    private List<String> options;

    public SingleLineRadioViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(final CaseField field) {
        String labelText = getLabel(field);

        if (isRequired(field)) {
            labelText += " (Required)";
        }
        options = field.getSelectOptions();
        firstOption.setText(options.get(0));
        secondOption.setText(options.get(1));
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == firstOption.getId()) {
                    CaseFieldValueCache.put(field.getDisplayName().get("en"), options.get(0));
                } else {
                    CaseFieldValueCache.put(field.getDisplayName().get("en"), options.get(1));
                }
            }
        });
        labelView.setHint(labelText);
        disableUnediatbleField(field, optionGroup);
        if (!TextUtils.isEmpty(CaseFieldValueCache.get(getLabel(field)))) {
            setSelectedRadio(CaseFieldValueCache.get(getLabel(field)));
        }
    }

    @Override
    public void setOnClickListener(CaseField field) {
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
