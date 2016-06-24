package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;

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
        disableUnediatbleField(valueView, field);
        valueView.setChecked(Boolean.valueOf(CaseFieldValueCache.get(getLabel(field))));
    }

    @Override
    public void setOnClickListener(final CaseField field) {
        valueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseFieldValueCache.put(getLabel(field),
                        String.valueOf(((CheckBox) v).isChecked()));
            }
        });
    }

    private void disableUnediatbleField(View view, CaseField field) {
        if (!isEditable(field)) {
            itemView.setBackgroundResource(R.color.gainsboro);
            view.setEnabled(false);
        }
    }
}
