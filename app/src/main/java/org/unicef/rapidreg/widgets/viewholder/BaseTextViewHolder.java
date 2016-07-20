package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordActivity;
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
        } else {
            itemValues.addItem(field.getName(), getResult());
        }
    }


    protected void initValueViewStatus() {
        if (!((RecordActivity) context).getCurrentFeature().isEditMode()) {
            getValueView().setEnabled(false);
            getValueView().setTextColor(context.getResources().getColor(R.color.gray));
        } else {
            getValueView().setEnabled(true);
            getValueView().setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    protected void initValueViewData(Field field) {
        if (isSubFormField(field)) {
            getValueView().setText(getValueForSubForm(field));
        } else {
            getValueView().setText(itemValues.getAsString(field.getName()));
        }
    }


    protected abstract String getResult();

    protected abstract TextView getValueView();
}
