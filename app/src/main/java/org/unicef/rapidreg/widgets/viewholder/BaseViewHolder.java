package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected View itemView;
    protected Context context;
    protected ItemValuesMap itemValues;
    private int currentPosition;

    public BaseViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
        this.itemValues = itemValues;
    }

    protected String getLabel(Field field) {
        return field.getDisplayName().get(Locale.getDefault().getLanguage());
    }

    protected boolean isEditable(Field field) {
        return field.isMarkForMobileField() ? false : field.isEditable();
    }

    protected boolean isRequired(Field field) {
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

    protected boolean isSubFormField(Field field) {
        return field.getParent() != null;
    }

    protected String getValueForSubForm(Field field) {
        if (itemValues == null) {
            return null;
        }

        Map<String, Object> value = itemValues.getValues();
        if (value.containsKey(field.getName())) {
            Object res = value.get(field.getName());
            return res instanceof List ? Utils.toStringResult((List<String>) res) : res.toString();
        }
        return null;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    protected Object getResult() {
        return null;
    }

    public abstract void setValue(T field);

    public abstract void setOnClickListener(T field);

    public abstract void setFieldEditable(boolean editable);
}
