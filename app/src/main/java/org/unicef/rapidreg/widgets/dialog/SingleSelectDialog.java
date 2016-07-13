package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.util.Arrays;

public class SingleSelectDialog extends BaseDialog {

    private String result;
    private String[] optionItems;

    public SingleSelectDialog(Context context, Field field,
                              ItemValues itemValues, TextView resultView, ViewSwitcher viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        String fieldType = field.getType();
        optionItems = getSelectOptions(fieldType, field);

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
