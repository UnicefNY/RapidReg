package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.internal.LazilyParsedNumber;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

public abstract class BaseTextViewHolder extends BaseViewHolder<Field> {
    public BaseTextViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
    }

    protected void saveValues(final Field field) {
        if (TextUtils.isEmpty(getValueView().getText())) {
            return;
        }
        if (field.isNumericField()) {
            itemValues.addItem(field.getName(), Integer.valueOf(getResult()));
        }else{
            itemValues.addItem(field.getName(), getResult());
        }
    }

    protected abstract String getResult();

    protected abstract TextView getValueView();
}
