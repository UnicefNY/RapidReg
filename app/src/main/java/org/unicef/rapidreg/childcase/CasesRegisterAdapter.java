package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.fielddialog.BaseDialog;
import org.unicef.rapidreg.childcase.fielddialog.FiledDialogFactory;
import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;

import java.util.List;

public class CasesRegisterAdapter extends ArrayAdapter<CaseField> {
    public static final String TAG = CasesRegisterAdapter.class.getSimpleName();

    public CasesRegisterAdapter(Context context, int resource, List<CaseField> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CaseField field = getItem(position);
        String fieldType = field.getType();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int resourceId = getFieldLayoutId(fieldType);

        if (resourceId > 0) {
            convertView = inflater.inflate(resourceId, null);
        }

        if ("separator".equals(field.getType())) {
            convertView.setVisibility(View.INVISIBLE);
        } else {
            String label = field.getDisplayName().get("en");

            TextView tvFormLabel = (TextView) convertView.findViewById(R.id.label);
            tvFormLabel.setText(label);

            if (Case.FieldType.TICK_BOX.name().equalsIgnoreCase(fieldType)) {
                CheckBox cbValue = (CheckBox) convertView.findViewById(R.id.value);
                cbValue.setChecked(Boolean.valueOf(CaseValues.getInstance().get(label)));
            } else {
                TextView tvValue = (TextView) convertView.findViewById(R.id.value);
                tvValue.setText(CaseValues.getInstance().get(label));
            }
        }

        if (!Case.FieldType.TICK_BOX.name().equalsIgnoreCase(fieldType)) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView valueView = (TextView) view.findViewById(R.id.value);
                    try {
                        showFieldDialog(field, valueView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            CheckBox cbValue = (CheckBox) convertView.findViewById(R.id.value);
            cbValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaseValues.getInstance().put(field.getDisplayName().get("en"),
                            String.valueOf(((CheckBox) v).isChecked()));
                }
            });
        }
        return convertView;
    }

    private void showFieldDialog(CaseField field, TextView valueView) {
        String fieldType = field.getType();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(field.getDisplayName().get("en"));

        if (fieldType.equals("select_box")) {
            fieldType = field.isMultiSelect() ? "multi_select_box" : "single_select_box";
        }
        try {
            BaseDialog dialog = FiledDialogFactory.createDialog(
                    Case.FieldType.valueOf(fieldType.toUpperCase()),
                    getContext(),
                    field,
                    valueView);
            dialog.show();
        } catch (DialogException e) {
            e.printStackTrace();
        }
    }


    private int getFieldLayoutId(String fieldType) {
        Resources resources = getContext().getResources();
        String packageName = getContext().getPackageName();

        if (Case.FieldType.TICK_BOX.name().equalsIgnoreCase(fieldType)) {
            return resources.getIdentifier("form_tick_box", "layout",
                    packageName);
        }
        return resources.getIdentifier("form_text_field", "layout",
                packageName);
    }
}
