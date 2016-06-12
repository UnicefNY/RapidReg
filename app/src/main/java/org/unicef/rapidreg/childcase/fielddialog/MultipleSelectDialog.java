package org.unicef.rapidreg.childcase.fielddialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultipleSelectDialog extends BaseDialog {

    private List<String> result;
    private String[] optionItems;

    public MultipleSelectDialog(Context context, CaseField caseField, TextView resultView) {
        super(context, caseField, resultView);
        result = new ArrayList<>();
    }

    @Override
    public void initView() {
        String fieldType = caseField.getType();
        optionItems = getSelectOptions(fieldType, caseField);

        boolean[] selectedValues = getSelectedValues(resultView.getText().toString().trim(), optionItems);

        getBuilder().setMultiChoiceItems(optionItems, selectedValues,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            result.add(optionItems[which]);
                        } else {
                            result.remove(optionItems[which]);
                        }
                    }
                });
    }

    private boolean[] getSelectedValues(String text, String[] optionItems) {
        boolean[] selectedValues = new boolean[optionItems.length];
        if (!"".equals(text)) {
            result.addAll(Arrays.asList(text.split(",")));
            for (String item : result) {
                int selected;
                if ((selected = Arrays.asList(optionItems).indexOf(item)) != -1) {
                    selectedValues[selected] = true;
                }
            }
        }
        return selectedValues;
    }

    @Override
    public String getResult() {
        StringBuilder strResult = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            if (i < result.size() - 1) {
                strResult.append(result.get(i) + ",");
            } else {
                strResult.append(result.get(i));
            }
        }
        return strResult.toString();
    }
}
