package org.unicef.rapidreg.widgets.helper;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.CaseService;

public class TickBoxHelper extends BaseWidgetHelper implements WidgetHelper {

    private View convertView;

    public TickBoxHelper(Context context, CaseField field) {
        super(context, field);
    }

    @Override
    public View getConvertView() {
        if (convertView == null) {
            int resId = resources.getIdentifier(CaseField.TYPE_TICK_BOX, "layout", packageName);
            convertView = inflater.inflate(resId, null);
        }

        return convertView;
    }

    @Override
    public void setValue() {
        TextView labelView = getLabelView();
        labelView.setText(getLabel());

        CheckBox valueView = getValueView();

        disableUnediatbleField(valueView);
        valueView.setChecked(Boolean.valueOf(CaseService.CaseValues.get(getLabel())));
    }

    @Override
    public void setOnClickListener() {
        CheckBox valueView = getValueView();
        valueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseService.CaseValues.put(getLabel(),
                        String.valueOf(((CheckBox) v).isChecked()));
            }
        });
    }

    private CheckBox getValueView() {
        return (CheckBox) getConvertView().findViewById(R.id.value);
    }

    private void disableUnediatbleField(View view) {
        if (!isEditable()) {
            convertView.setBackgroundResource(R.color.gainsboro);
            view.setEnabled(false);
        }
    }
}
