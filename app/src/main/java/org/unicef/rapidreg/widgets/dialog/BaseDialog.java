package org.unicef.rapidreg.widgets.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseDialog {
    protected Field field;
    protected TextView resultView;
    protected ViewSwitcher viewSwitcher;
    protected ItemValuesMap itemValues;

    private AlertDialog.Builder builder;
    protected Context context;

    public BaseDialog(final Context context, final Field field, final ItemValuesMap itemValues,
                      final TextView resultView, final ViewSwitcher viewSwitcher) {
        this.field = field;
        this.resultView = resultView;
        this.viewSwitcher = viewSwitcher;
        this.context = context;
        this.itemValues = itemValues;

        builder = new AlertDialog.Builder(context);
        builder.setTitle(field.getDisplayName().get(Locale.getDefault().getLanguage()));

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getResult() != null && !TextUtils.isEmpty(getResult().toString())) {
                    BaseDialog.this.viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
                } else {
                    BaseDialog.this.viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
                }
                BaseDialog.this.resultView.setText(getDisplayText());

                itemValues.addItem(field.getName(), getResult());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void show() {
        initView();
        AlertDialog dialog = builder.show();

        Utils.changeDialogDividerColor(context, dialog);
    }

    public static String[] getSelectOptions(String fieldType, Field field) {
        String language = Locale.getDefault().getLanguage();
        List<CharSequence> items = new ArrayList<>();

        List<Object> options = field.getOptionStringsText().get(language);
        if (options.get(0) instanceof Map) {
            List<Map<String, String>> arrayList = field.getOptionStringsText().get(language);
            for (Map<String, String> map : arrayList) {
                items.add(map.get("display_text"));
            }
        } else {
            items = field.getOptionStringsText().get(language);
        }

        return items.toArray(new String[0]);
    }

    protected AlertDialog.Builder getBuilder() {
        return builder;
    }

    protected Context getContext() {
        return context;
    }

    protected String getDisplayText() {
        return getResult() == null ? null : getResult().toString();
    }

    public abstract void initView();

    public abstract Object getResult();
}
