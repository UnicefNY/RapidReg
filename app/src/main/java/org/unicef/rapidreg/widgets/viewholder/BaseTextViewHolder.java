package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.ItemValues;

public abstract class BaseTextViewHolder extends BaseViewHolder<CaseField> {
    public BaseTextViewHolder(Context context, View itemView, ItemValues itemValues) {
        super(context, itemView, itemValues);
    }

    protected void saveValues(final CaseField field) {
        if (TextUtils.isEmpty(getValueView().getText())) {
            return;
        }

        itemValues.addStringItem(field.getName(), getResult());
    }

    protected abstract String getResult();

    protected abstract TextView getValueView();
}
