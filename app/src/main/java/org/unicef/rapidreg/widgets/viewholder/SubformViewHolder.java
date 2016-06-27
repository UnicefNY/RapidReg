package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseRegisterAdapter;
import org.unicef.rapidreg.forms.childcase.CaseField;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubformViewHolder extends BaseViewHolder<CaseField> {
    private CaseActivity activity;

    @BindView(R.id.field_list)
    RecyclerView fieldList;

    List<CaseField> fields;

    public SubformViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, this.itemView);
        activity = (CaseActivity) context;
    }

    @Override
    public void setValue(CaseField field) {
        fields = field.getSubForm().getFields();
        CaseRegisterAdapter adapter = new CaseRegisterAdapter(activity, fields);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(activity);
        layout.setAutoMeasureEnabled(true);
        fieldList.setLayoutManager(layout);
        fieldList.setAdapter(adapter);
    }

    @Override
    public void setOnClickListener(CaseField field) {
    }
}
