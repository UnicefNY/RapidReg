package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.unicef.rapidreg.forms.childcase.CaseField;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected View itemView;
    protected Context context;

    public BaseViewHolder(Context context, View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
    }

    public void setValue(T field){}

    public void setOnClickListener(T field){}

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
}
