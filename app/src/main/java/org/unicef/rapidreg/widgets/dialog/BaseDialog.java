package org.unicef.rapidreg.widgets.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;

public abstract class BaseDialog {
    protected CaseField caseField;
    protected TextView resultView;
    protected ViewSwitcher viewSwitcher;

    private AlertDialog.Builder builder;
    private Context context;

    public BaseDialog(final Context context, final CaseField caseField,
                      final TextView resultView) {
        this(context, caseField, resultView, null);
    }

    public BaseDialog(final Context context, final CaseField caseField,
                      final TextView resultView, final ViewSwitcher viewSwitcher) {
        this.caseField = caseField;
        this.resultView = resultView;
        this.viewSwitcher = viewSwitcher;
        this.context = context;

        builder = new AlertDialog.Builder(context);
        builder.setTitle(caseField.getDisplayName().get(Locale.getDefault().getLanguage()));

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(getResult())) {
                    BaseDialog.this.viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_HAS_ANSWER_STATE);
                } else {
                    BaseDialog.this.viewSwitcher.setDisplayedChild(GenericViewHolder.FORM_NO_ANSWER_STATE);
                }
                BaseDialog.this.resultView.setText(getResult());

                if (isSubformField()) {
                    SubformCache.put(caseField.getParent(), getValues());
                } else {
                    String language = Locale.getDefault().getLanguage();
                    CaseFieldValueCache.put(caseField.getDisplayName().get(language), getResult());
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

    public abstract void initView();

    public abstract String getResult();

    private boolean isSubformField() {
        return caseField.getParent() != null;
    }

    private List<Map<String, String>> getValues() {
        String language = Locale.getDefault().getLanguage();
        List<Map<String, String>> values = SubformCache.get(caseField.getParent()) == null ?
                new ArrayList<Map<String, String>>() : SubformCache.get(caseField.getParent());

        Map<String, String> value;
        try {
            value = values.get(caseField.getIndex());
            value.put(caseField.getDisplayName().get(language), getResult());
            values.set(caseField.getIndex(), value);
        } catch (IndexOutOfBoundsException e) {
            value = new HashMap<>();
            value.put(caseField.getDisplayName().get(language), getResult());
            values.add(caseField.getIndex(), value);
        }

        return values;
    }
}
