package org.unicef.rapidreg.childcase.fielddialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class MultipleSelectDialog extends BaseDialog {

    private String result;
    private String[] optionItems;

    public MultipleSelectDialog(Context context, CaseField caseField, TextView resultView) {
        super(context, caseField, resultView);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        String fieldType = caseField.getType();
        optionItems = getSelectOptions(fieldType, caseField);

        getBuilder().setMultiChoiceItems(optionItems, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        
                    }
                });
    }

    @Override
    public String getResult() {
        return result;
    }
}
