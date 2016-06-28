package org.unicef.rapidreg.widgets.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDialog {
    protected CaseField caseField;
    protected TextView resultView;

    private AlertDialog.Builder builder;
    private Context context;

    public BaseDialog(final Context context, final CaseField caseField, final TextView resultView) {
        this.caseField = caseField;
        this.resultView = resultView;
        this.context = context;

        builder = new AlertDialog.Builder(context);
        builder.setTitle(caseField.getDisplayName().get("en"));

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseDialog.this.resultView.setText(getResult());

                if (isSubformField()) {
                    SubformCache.put(caseField.getParent(), getValues());
                } else {
                    CaseFieldValueCache.put(caseField.getDisplayName().get("en"), getResult());
                }

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
        builder.show();
    }

    public static String[] getSelectOptions(String fieldType, CaseField field) {
        List<CharSequence> items = new ArrayList<>();
        if (fieldType.equals(CaseField.TYPE_MULTI_SELECT_BOX)) {
            List<Map<String, String>> arrayList = field.getOptionStringsText().get("en");
            for (Map<String, String> map : arrayList) {
                items.add(map.get("display_text"));
            }
        } else {
            items = field.getOptionStringsText().get("en");
        }
        return items.toArray(new String[0]);
    }

    protected AlertDialog.Builder getBuilder() {
        return builder;
    }

    protected Context getContext() {
        return context;
    }

    public abstract void initView();

    public abstract String getResult();

    private boolean isSubformField() {
        return caseField.getParent() != null;
    }

    private List<Map<String, String>> getValues() {
        List<Map<String, String>> values = SubformCache.get(caseField.getParent()) == null ?
                new ArrayList<Map<String, String>>() : SubformCache.get(caseField.getParent());

        Map<String, String> value;
        try {
            value = values.get(caseField.getIndex());
            value.put(caseField.getDisplayName().get("en"), getResult());
            values.set(caseField.getIndex(), value);
        } catch (IndexOutOfBoundsException e) {
            value = new HashMap<>();
            value.put(caseField.getDisplayName().get("en"), getResult());
            values.add(caseField.getIndex(), value);
        }

        return values;
    }
}
