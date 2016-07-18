package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultipleSelectDialog extends BaseDialog {

    private List<String> result;
    private String[] optionItems;

    public MultipleSelectDialog(Context context, Field field, ItemValuesMap itemValues, TextView resultView, ViewSwitcher viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        result = new ArrayList<>();
    }

    @Override
    public void initView() {
        String fieldType = field.getType();
        optionItems = getSelectOptions(fieldType, field);

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
    public List<String> getResult() {
        return result;
    }
}
