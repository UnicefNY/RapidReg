package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;

import java.util.List;

public class CasesRegisterAdapter extends ArrayAdapter<CaseField> {

    public CasesRegisterAdapter(Context context, int resource, List<CaseField> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CaseField caseField = getItem(position);
        String fieldType = caseField.getType();

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            int resourceId = getFieldLayoutId(fieldType);
            if (resourceId > 0) {
                convertView = inflater.inflate(resourceId, null);
            }
        }

        if ("separator".equals(caseField.getType())) {
            convertView.setVisibility(View.INVISIBLE);
        } else {
            TextView field = (TextView) convertView.findViewById(R.id.label);
            field.setText(caseField.getDisplayName().get("en"));
            field.setPadding(15, 15, 0, 0);
        }
        return convertView;
    }

    private int getFieldLayoutId(String fieldType) {
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();

        if (fieldType.equals("tick_box")) {
            return resources.getIdentifier("form_tick_box", "layout",
                    packageName);
        }
        return resources.getIdentifier("form_text_field", "layout",
                packageName);
    }
}
