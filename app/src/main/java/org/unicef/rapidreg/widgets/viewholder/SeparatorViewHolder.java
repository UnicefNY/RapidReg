package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class SeparatorViewHolder extends BaseViewHolder<CaseField> {

    public SeparatorViewHolder(Context context, View itemView) {
        super(context, itemView);
    }

    @Override
    public void setValue(CaseField field) {
        itemView.setVisibility(View.GONE);
    }

    @Override
    public void setOnClickListener(CaseField field) {

    }

    @Override
    protected String getResult() {
        return null;
    }
}
