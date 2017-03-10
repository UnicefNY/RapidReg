package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected View itemView;
    protected Context context;
    protected ItemValuesMap itemValues;
    protected ItemValuesMap fieldValueVerifyResult;
    private int currentPosition;

    public BaseViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
        this.itemValues = itemValues;
    }

    public void setFieldValueVerifyResult(ItemValuesMap fieldValueVerifyResult) {
        this.fieldValueVerifyResult = fieldValueVerifyResult;
    }

    protected String getLabel(Field field) {
        return field.getDisplayName().get(PrimeroAppConfiguration.getDefaultLanguage());
    }

    public boolean isEditable(Field field) {
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

    protected String getValue(Field field) {
        if (itemValues == null || !itemValues.getValues().containsKey(field.getName())) {
            return null;
        }
        Map<String, Object> value = itemValues.getValues();
        Object res = value.get(field.getName());

        if (!(res instanceof List)) {
            return res.toString();
        }

        List<String> optionValues = new ArrayList<>();
        if (field.isMultiSelect()) {
            List<String> optionKeys = (List<String>) res;
            List<String> selectOptionKeys = field.getSelectOptionKeysIfMultiple();
            List<String> selectOptionValues = field.getSelectOptionValuesIfSelectable();
            for (String optionKey : optionKeys) {
                optionValues.add(selectOptionValues.get(selectOptionKeys.indexOf(optionKey)));
            }
        }
        return Utils.toStringResult(optionValues);
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

    public abstract void setFieldClickable(boolean clickable);
}
