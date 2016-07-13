package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;

import org.unicef.rapidreg.forms.Field;

public class SeparatorViewHolder extends BaseViewHolder<Field> {

    public SeparatorViewHolder(Context context, View itemView) {
        super(context, itemView, null);
    }

    @Override
    public void setValue(Field field) {
        itemView.setVisibility(View.GONE);
    }

    @Override
    public void setOnClickListener(Field field) {

    }

    @Override
    public void setFieldEditable(boolean editable) {

    }
}
