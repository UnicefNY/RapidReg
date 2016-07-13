package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.JsonObject;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.util.Locale;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected View itemView;
    protected Context context;
    protected ItemValues itemValues;

    public BaseViewHolder(Context context, View itemView, ItemValues itemValues) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
        this.itemValues = itemValues;
    }

    public abstract void setValue(T field);

    public abstract void setOnClickListener(T field);

    protected String getLabel(Field field) {
        return field.getDisplayName().get(Locale.getDefault().getLanguage());
    }

    protected boolean isEditable(Field field) {
        return field.isEditable();
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

        JsonObject value = itemValues.getValues();
        if (value.has(field.getName())) {
            return value.get(field.getName()).getAsString();
        }
        return null;
    }

    protected Object getResult() {
        return null;
    }

    public abstract void setFieldEditable(boolean editable);
}
