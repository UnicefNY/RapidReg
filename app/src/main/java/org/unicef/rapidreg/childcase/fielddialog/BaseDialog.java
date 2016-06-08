package org.unicef.rapidreg.childcase.fielddialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseDialog {
    private AlertDialog.Builder builder;
    private Context context;

    protected CaseField caseField;
    protected TextView resultView;

    public BaseDialog(Context context, CaseField caseField, TextView resultView) {
        this.caseField = caseField;
        this.resultView = resultView;
        this.context = context;

        builder = new AlertDialog.Builder(context);
        builder.setTitle(caseField.getDisplayName().get("en"));

        initView();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseDialog.this.resultView.setText(getResult());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public abstract void initView();

    public abstract String getResult();

    public void show() {
        builder.show();
    }

    protected Context getContext() {
        return context;
    }

    protected AlertDialog.Builder getBuilder() {
        return builder;
    }

    public static String[] getSelectOptions(String fieldType, CaseField field) {
        List<CharSequence> items = new ArrayList<>();
        if (fieldType.equals("multi_select_box")) {
            List<Map<String, String>> arrayList = field.getOptionStringsText().get("en");
            for (Map<String, String> map : arrayList) {
                items.add(map.get("display_text"));
            }
        } else {
            items = field.getOptionStringsText().get("en");
        }
        return items.toArray(new String[0]);
    }
}
