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
import java.util.Map;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected View itemView;
    protected Context context;

    public BaseViewHolder(Context context, View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
    }

    public void setValue(T field) {
    }

    public void setOnClickListener(T field) {
    }

    //TODO: need to get display name according to the current system language
    protected String getLabel(CaseField field) {
        return field.getDisplayName().get("en");
    }

    protected boolean isEditable(CaseField field) {
        return field.isEditable();
    }

    protected boolean isRequired(CaseField field) {
        return field.isRequired();
    }

    protected void disableUnediatbleField(CaseField field, View view) {
        if (isEditable(field)) {
            itemView.setBackgroundResource(R.color.white);
            itemView.setEnabled(true);
            if (view != null) {
                view.setEnabled(true);
            }
        } else {
            itemView.setBackgroundResource(R.color.gainsboro);
            itemView.setEnabled(false);
            if (view != null) {
                view.setEnabled(false);
            }
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

        return value.get(getLabel(field));
    }

    protected List<Map<String, String>> getValues(CaseField field) {
        List<Map<String, String>> values = SubformCache.get(field.getParent()) == null ?
                new ArrayList<Map<String, String>>() : SubformCache.get(field.getParent());

        Map<String, String> value;
        try {
            value = values.get(field.getIndex());
            value.put(field.getDisplayName().get("en"), getResult());
            values.set(field.getIndex(), value);
        } catch (IndexOutOfBoundsException e) {
            value = new HashMap<>();
            value.put(field.getDisplayName().get("en"), getResult());
            values.add(field.getIndex(), value);
        }

        return values;
    }

    protected abstract String getResult();
}
