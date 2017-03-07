package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.GlobalLocationCache;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;

import java.util.Arrays;
import java.util.Locale;

public class SingleSelectDialog extends BaseDialog {

    private String result;
    private String[] optionItems;

    SearchAbleDialog dialog;

    public SingleSelectDialog(Context context, Field field,
                              ItemValuesMap itemValues, TextView resultView, ViewSwitcher
                                      viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        optionItems = getSelectOptions(field);

        simplifyLocationOptionsIfLocationFiled();

        int selectIndex = Arrays.asList(optionItems).indexOf(result);
        if (selectIndex == -1) {
            result = "";
        } else {
            result = optionItems[selectIndex];
        }
        dialog = new SearchAbleDialog(context, field.getDisplayName().get(Locale.getDefault()
                .getLanguage()), optionItems, selectIndex);

        dialog.setOnClick(result -> SingleSelectDialog.this.result = result);

        dialog.setCancelButton(v -> dialog.dismiss());

        dialog.setOkButton(view -> {
            if (getResult() != null && !TextUtils.isEmpty(getResult().toString())) {
                viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
            } else {
                viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
            }
            resultView.setText(getResult() == null ? null : getResult().toString());

            itemValues.addItem(field.getName(), recoveryLocationValueIfLocationFiled());
            dialog.dismiss();
        });

    }

    private void simplifyLocationOptionsIfLocationFiled() {
        if (field.isSelectField() && GlobalLocationCache.containsKey(field.getName())) {
            GlobalLocationCache.initSimpleLocations(optionItems);
            optionItems = GlobalLocationCache.getSimpleLocations().toArray(new String[0]);
        }
    }

    private String recoveryLocationValueIfLocationFiled() {
        if (field.isSelectField() && GlobalLocationCache.containsKey(field.getName()) && !org.unicef.rapidreg.utils
                .TextUtils.isEmpty(getResult())) {
            return Arrays.asList(getSelectOptions(field)).get(GlobalLocationCache.index(getResult()));
        }
        return getResult();
    }

    @Override
    public void show() {
        initView();

        dialog.show();
        changeDialogDividerColor(context, dialog);
    }

    @Override
    public String getResult() {
        return result;
    }
}
