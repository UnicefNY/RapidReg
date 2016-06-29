package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.widgets.dialog.BaseDialog;
import org.unicef.rapidreg.widgets.dialog.FiledDialogFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenericViewHolder extends BaseViewHolder<CaseField> {

    public static final String TAG = GenericViewHolder.class.getSimpleName();
    public static final int FORM_NO_ANSWER_STATE = 0;
    public static final int FORM_HAS_ANSWER_STATE = 1;

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.value)
    TextView valueView;

    @BindView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;

    @BindView(R.id.form_question)
    TextView formQuestion;

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

//        labelView.setText(labelText);
        labelView.setHint(labelText);
        formQuestion.setHint(labelText);
        disableUnediatbleField(field);
        if (isSubformField(field)) {

            valueView.setText(getValue(field));
        } else {
            valueView.setText(CaseFieldValueCache.get(getLabel(field)));
        }
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

    @Override
    protected void disableUnediatbleField(CaseField field) {
        super.disableUnediatbleField(field);

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
                    valueView,
                    viewSwitcher);
            dialog.show();
        } catch (DialogException e) {
            Log.e(TAG, String.format("create dialog error. Field: %s", field), e);
        }
    }
}
