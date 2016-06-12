package org.unicef.rapidreg.widgets.helper;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.fielddialog.BaseDialog;
import org.unicef.rapidreg.childcase.fielddialog.FiledDialogFactory;
import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.CaseService;

public class GenericHelper extends BaseWidgetHelper implements WidgetHelper {
    public static final String TAG = GenericHelper.class.getSimpleName();

    private View convertView;

    public GenericHelper(Context context, CaseField field) {
        super(context, field);
    }

    @Override
    public View getConvertView() {
        if (convertView == null) {
            int resId = resources.getIdentifier(CaseField.TYPE_TEXT_FIELD, "layout", packageName);
            convertView = inflater.inflate(resId, null);
        }

        return convertView;
    }

    @Override
    public void setValue() {
        TextView labelView = getLabelView();
        labelView.setText(getLabel());

        TextView valueView = getValueView();
        valueView.setText(CaseService.CaseValues.get(getLabel()));
    }

    @Override
    public void setOnClickListener() {
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView valueView = getValueView();
                try {
                    showFieldDialog(field, valueView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showFieldDialog(CaseField field, TextView valueView) {
        String fieldType = field.getType();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getLabel());

        if (fieldType.equals(CaseField.TYPE_SELECT_BOX)) {
            fieldType = field.isMultiSelect() ? CaseField.TYPE_MULTI_SELECT_BOX :
                    CaseField.TYPE_SINGLE_SELECT_BOX;
        }
        try {
            BaseDialog dialog = FiledDialogFactory.createDialog(
                    CaseField.FieldType.valueOf(fieldType.toUpperCase()),
                    context,
                    field,
                    valueView);
            dialog.show();
        } catch (DialogException e) {
            Log.e(TAG, String.format("create dialog error. Field: %s", field), e);
        }
    }

    private TextView getValueView() {
        return (TextView) getConvertView().findViewById(R.id.value);
    }
}
