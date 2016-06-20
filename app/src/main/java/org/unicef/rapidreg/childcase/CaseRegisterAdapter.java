package org.unicef.rapidreg.childcase;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.widgets.viewholder.BaseViewHolder;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;
import org.unicef.rapidreg.widgets.viewholder.SeparatorViewHolder;
import org.unicef.rapidreg.widgets.viewholder.TickBoxViewHolder;

import java.io.Serializable;
import java.util.List;

public class CaseRegisterAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int GENERIC_VIEW_HOLDER = 0;
    private final static int SEPARATOR_VIEW_HOLDER = 1;
    private final static int TICK_BOX_VIEW_HOLDER = 2;

    private List<CaseField> fields;
    private Context context;
    protected LayoutInflater inflater;
    protected Resources resources;
    protected String packageName;

    public CaseRegisterAdapter(Context context, List<CaseField> fields) {
        this.fields = fields;
        this.context = context;
        inflater = LayoutInflater.from(context);
        resources = context.getResources();
        packageName = context.getPackageName();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case GENERIC_VIEW_HOLDER:
                return new GenericViewHolder(context, inflater.inflate(resources
                        .getIdentifier(CaseField.TYPE_TEXT_FIELD, "layout", packageName), null));
            case TICK_BOX_VIEW_HOLDER:
                return new TickBoxViewHolder(context, inflater.inflate(resources
                        .getIdentifier(CaseField.TYPE_TICK_BOX, "layout", packageName), null));
            default:
                return new SeparatorViewHolder(context, new View(context));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CaseField field = fields.get(position);
        holder.setValue(field);
        Serializable caseMode = ((Activity) context).getIntent()
                .getSerializableExtra(CaseActivity.INTENT_KEY_CASE_MODE);
        if (CaseActivity.CaseMode.DETAIL != caseMode) {
            holder.setOnClickListener(field);
        }
    }

    @Override
    public int getItemViewType(int position) {
        CaseField field = fields.get(position);
        if (field.isSeparator()) {
            return SEPARATOR_VIEW_HOLDER;
        }
        if (field.isTickBox()) {
            return TICK_BOX_VIEW_HOLDER;
        }
        return GENERIC_VIEW_HOLDER;
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

}
