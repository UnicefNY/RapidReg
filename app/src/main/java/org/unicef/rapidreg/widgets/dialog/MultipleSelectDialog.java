package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MultipleSelectDialog extends BaseDialog {

    private List<String> results;
    private String[] optionItems;

    private SearchAbleMultiSelectDialog dialog;

    public MultipleSelectDialog(Context context, Field field, ItemValuesMap itemValues, TextView
            resultView, ViewSwitcher viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        results = new ArrayList<>();
    }

    @Override
    public void initView() {
        String fieldType = field.getType();
        optionItems = getSelectOptions(fieldType, field);
        results.addAll(itemValues.getAsList(field.getName()));

        dialog = new SearchAbleMultiSelectDialog(context, field.getDisplayName().get(Locale.getDefault()
                .getLanguage()), optionItems, results);

        dialog.disableClearButton(true);
        dialog.disableDialogFilter(true);
        dialog.setOnClick(new SearchAbleMultiSelectDialog.SearchAbleMultiSelectDialogOnClickListener() {
            @Override
            public void onClick(List<String> results) {
                MultipleSelectDialog.this.results = results;
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
                resultView.setText(getDisplayText());

                itemValues.addItem(field.getName(), getResult());
                dialog.dismiss();
            }
        });
    }

    @Override
    public void show() {
        initView();

        dialog.show();
        changeDialogDividerColor(context, dialog);
    }

    @Override
    public List<String> getResult() {
        return results;
    }

    @Override
    protected String getDisplayText() {
        return results == null ? null : Utils.toStringResult(results);
    }

}
