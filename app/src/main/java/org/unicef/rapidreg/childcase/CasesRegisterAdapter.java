package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.fielddialog.BaseDialog;
import org.unicef.rapidreg.childcase.fielddialog.FiledDialogFactory;
import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CasesRegisterAdapter extends ArrayAdapter<CaseField> {

    private static final String TAG = CasesRegisterAdapter.class.getSimpleName();

    public CasesRegisterAdapter(Context context, int resource, List<CaseField> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CaseField field = getItem(position);
        String fieldType = field.getType();

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            int resourceId = getFieldLayoutId(fieldType);
            if (resourceId > 0) {
                convertView = inflater.inflate(resourceId, null);
            }
        }

        if ("separator".equals(field.getType())) {
            convertView.setVisibility(View.INVISIBLE);
        } else {
            TextView tvFormLabel = (TextView) convertView.findViewById(R.id.label);
            tvFormLabel.setText(field.getDisplayName().get("en"));
        }

        if (!Case.FieldType.TICK_BOX.name().equalsIgnoreCase(fieldType)) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), view.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                    TextView valueView = (TextView) view.findViewById(R.id.value);
                    try {
                        showFieldDialog(field, valueView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return convertView;
    }

    private void showFieldDialog(CaseField field, TextView valueView) {
        String value = valueView.getText().toString();

        String fieldType = field.getType();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(field.getDisplayName().get("en"));

        if (fieldType.equals("select_box")) {
            fieldType = field.isMultiSelect() ? "multi_select_box" : "single_select_box";
        }
        Log.i(TAG, fieldType);
        Toast.makeText(getContext(), fieldType, Toast.LENGTH_SHORT).show();
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


//        switch (fieldType) {
//            case "text_field":
//                EditText textSingle = new EditText(getContext());
//                textSingle.setText(value);
//                builder.setView(textSingle);
//                break;
//
//            case "textarea":
//                EditText textMultiple = new EditText(getContext());
//                textMultiple.setText(value);
//                textMultiple.setSingleLine(false);
//                builder.setView(textMultiple);
//                break;
//
//            case "radio_button":
//                Log.i(TAG, "value1: " + value);
//                optionItems = getSelectOptions(fieldType, field);
//
//            case "single_select_box":
//                builder.setSingleChoiceItems(optionItems,
//                        Integer.valueOf(value), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Log.i(TAG, "value2: " + which);
//                            }
//                        });
//                break;
//
//            case "multi_select_box":
//                builder.setMultiChoiceItems(optionItems, null,
//                        new DialogInterface.OnMultiChoiceClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//                            }
//                        });
//                break;
//
//
//            case "numeric_field":
//                final EditText input = new EditText(getContext());
//                input.setInputType(InputType.TYPE_CLASS_NUMBER);
//                input.setRawInputType(Configuration.KEYBOARD_12KEY);
//                input.setText(value);
//                builder.setView(input);
//                break;
//
//            case "date_field":
//                DatePicker picker = new DatePicker(getContext());
//                picker.setCalendarViewShown(false);
//                picker.updateDate(2016, 4, 7);
//                builder.setView(picker);
//                int year = picker.getYear();
//                int month = picker.getMonth();
//                int day = picker.getDayOfMonth();
//
//                break;
//
//            default:
//                break;
//        }
//        builder.show();
//    }
//


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
