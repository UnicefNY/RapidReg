package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.widgets.dialog.BaseDialog;
import org.unicef.rapidreg.widgets.dialog.FiledDialogFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenericViewHolder extends BaseViewHolder<CaseField> {

    public static final String TAG = GenericViewHolder.class.getSimpleName();

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.value)
    TextView valueView;

    public GenericViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(CaseField field) {
        String labelText = getLabel(field);

        if (isRequired(field)) {
            labelText += " (Required)";
        }

        labelView.setText(labelText);
        disableUneditableField(itemView, field);
        valueView.setText(CaseService.CaseValues.get(getLabel(field)));
    }

    @Override
    public void setOnClickListener(final CaseField field) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showFieldDialog(field, valueView);
                } catch (Exception e) {
                    Log.e(TAG, String.format("show dialog error. Field: %s", field), e);
                }
            }
        });
    }

    private void disableUneditableField(View view, CaseField field) {
        if (!isEditable(field)) {
            itemView.setBackgroundResource(R.color.gainsboro);
            view.setEnabled(false);
        }
    }

    private void showFieldDialog(CaseField field, TextView valueView) {
        String fieldType = field.getType();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getLabel(field));

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

}
