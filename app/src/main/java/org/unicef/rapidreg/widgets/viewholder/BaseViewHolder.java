package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected View itemView;
    protected Context context;

    public BaseViewHolder(Context context, View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
    }

    public abstract void setValue(T field);

    public abstract void setOnClickListener(T field);

    protected String getLabel(CaseField field) {
        return field.getDisplayName().get(Locale.getDefault().getLanguage());
    }

    protected boolean isEditable(CaseField field) {
        return field.isEditable();
    }

    protected boolean isRequired(CaseField field) {
        return field.isRequired();
    }

    protected void disableUneditableField(boolean editable, View view) {
        if (editable) {
            itemView.setEnabled(true);
            if (view != null) {
                view.setEnabled(true);
            }
        } else {
            itemView.setEnabled(false);
            if (view != null) {
                view.setEnabled(false);
            }
        }
    }

    protected void setEditableBackgroundStyle(boolean editable) {
        if (editable) {
            itemView.setBackgroundResource(R.color.white);
        } else {
            itemView.setBackgroundResource(R.color.lighter_gray);
        }
    }

    protected boolean isSubformField(CaseField field) {
        return field.getParent() != null;
    }

    protected String getValue(CaseField field) {
        List<Map<String, String>> values = SubformCache.get(field.getParent()) == null ?
                new ArrayList<Map<String, String>>() : SubformCache.get(field.getParent());
        Map<String, String> value;

        try {
            value = values.get(field.getIndex());
        } catch (IndexOutOfBoundsException e) {
            value = new ArrayMap<>();
        }

        return value.get(field.getName());
    }

    protected List<Map<String, String>> getValues(CaseField field) {
        String language = Locale.getDefault().getLanguage();
        List<Map<String, String>> values = SubformCache.get(field.getParent()) == null ?
                new ArrayList<Map<String, String>>() : SubformCache.get(field.getParent());

        Map<String, String> value;
        try {
            value = values.get(field.getIndex());
            value.put(field.getDisplayName().get(language), getResult());
            values.set(field.getIndex(), value);
        } catch (IndexOutOfBoundsException e) {
            value = new HashMap<>();
            value.put(field.getDisplayName().get(language), getResult());
            values.add(field.getIndex(), value);
        }

        return values;
    }

    protected abstract String getResult();

    public abstract void setFieldEditable(boolean editable);
}
