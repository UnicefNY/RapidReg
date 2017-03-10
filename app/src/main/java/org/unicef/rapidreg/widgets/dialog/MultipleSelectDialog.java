package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MultipleSelectDialog extends BaseDialog {

    private List<String> results;
    private String[] optionItems;
    private List<String> optionItemKeys;

    private SearchAbleMultiSelectDialog dialog;

    public MultipleSelectDialog(Context context, Field field, ItemValuesMap itemValues, TextView
            resultView, ViewSwitcher viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        results = new ArrayList<>();
    }

    @Override
    public void initView() {
        optionItems = getSelectOptions(field);
        optionItemKeys = getSelectOptionKeys(field);

        results.addAll(parseKeysAsDisplayTexts(itemValues.getAsList(field.getName())));

        dialog = new SearchAbleMultiSelectDialog(context, field.getDisplayName().get(Locale.getDefault()
                .getLanguage()), optionItems, results);

        dialog.disableClearButton(true);
        dialog.disableDialogFilter(true);
        dialog.setOnClick(items -> MultipleSelectDialog.this.results = items);

        dialog.setCancelButton(view -> dialog.dismiss());

        dialog.setOkButton(view -> {
            if (getResult() != null && !TextUtils.isEmpty(getResult().toString())) {
                viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
            } else {
                viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
            }
            resultView.setText(getDisplayText());

            itemValues.addItem(field.getName(), parseDisplayTextsAsKeys(getResult()));
            dialog.dismiss();
        });
    }

    private List<String> parseKeysAsDisplayTexts(List<String> keys) {
        List<String> result = new ArrayList<>();
        for (String key : keys) {
            result.add(optionItems[optionItemKeys.indexOf(key)]);
        }
        return result;
    }

    private List<String> parseDisplayTextsAsKeys(List<String> displayTexts) {
        List<String> result = new ArrayList<>();
        for (String displayText : displayTexts) {
            result.add(optionItemKeys.get(Arrays.asList(optionItems).indexOf(displayText)));
        }
        return result;
    }

    private static List<String> getSelectOptionKeys(Field field) {
        String language = PrimeroAppConfiguration.getDefaultLanguage();
        List<String> items = new ArrayList<>();

        List<Map<String, String>> arrayList = field.getOptionStringsText().get(language);
        for (Map<String, String> map : arrayList) {
            items.add(map.get("id"));
        }
        return items;
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
