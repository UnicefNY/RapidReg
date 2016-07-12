package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.util.Arrays;

public class SingleSelectDialog extends BaseDialog {

    private String result;
    private String[] optionItems;

    public SingleSelectDialog(Context context, CaseField caseField,
                              ItemValues itemValues, TextView resultView, ViewSwitcher viewSwitcher) {
        super(context, caseField, itemValues, resultView, viewSwitcher);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        String fieldType = caseField.getType();
        optionItems = getSelectOptions(fieldType, caseField);

        int selectIndex = Arrays.asList(optionItems).indexOf(result);
        selectIndex = selectIndex == -1 ? 0 : selectIndex;
        result = optionItems[selectIndex];

        getBuilder().setSingleChoiceItems(optionItems,
                selectIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result = optionItems[which];
                    }
                });
    }

    @Override
    public String getResult() {
        return result;
    }
}
