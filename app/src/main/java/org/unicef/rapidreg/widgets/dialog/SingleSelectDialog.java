package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;

import java.util.Arrays;
import java.util.Locale;

public class SingleSelectDialog extends BaseDialog {

    private String result;
    private String[] optionItems;

    SearchAbleDialog dialog;

    public SingleSelectDialog(Context context, Field field,
                              ItemValuesMap itemValues, TextView resultView, ViewSwitcher viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        String fieldType = field.getType();
        optionItems = getSelectOptions(fieldType, field);

        int selectIndex = Arrays.asList(optionItems).indexOf(result);
        if (selectIndex == -1) {
            result = "";
        } else {
            result = optionItems[selectIndex];
        }
        //selectIndex = selectIndex == -1 ? 0 : selectIndex;


        dialog = new SearchAbleDialog(context, field.getDisplayName().get(Locale.getDefault().getLanguage()), optionItems, selectIndex);

        dialog.setOnClick(new SearchAbleDialog.SearchAbleDialogOnClickListener() {
            @Override
            public void onClick(String result) {
                SingleSelectDialog.this.result = result;
            }
        });

        dialog.setCancelButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOkButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResult() != null && !TextUtils.isEmpty(getResult().toString())) {
                    viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
                } else {
                    viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
                }
                resultView.setText(getResult() == null ? null : getResult().toString());

                itemValues.addItem(field.getName(), getResult());
                dialog.dismiss();
            }
        });

    }

    @Override
    public void show() {
        initView();

        dialog.show();
    }

    @Override
    public String getResult() {
        return result;
    }
}
