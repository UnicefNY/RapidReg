package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseRegisterAdapter;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubformViewHolder extends BaseViewHolder<CaseField> {

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.value)
    TextView valueView;

    private CaseActivity activity;
    private ViewGroup parent;
    private LinearLayout container;
    private List<CaseField> fields;
    private RecyclerView fieldList;

    public SubformViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        activity = (CaseActivity) context;
        parent = (ViewGroup) itemView;

        initView();
    }

    @Override
    public void setValue(CaseField field) {
        fields = field.getSubForm().getFields();
        String labelText = getLabel(field);

        if (isRequired(field)) {
            labelText += " (Required)";
        }

        labelView.setText(labelText);
        disableUnediatbleField(field);
        valueView.setText(CaseFieldValueCache.get(getLabel(field)));
    }

    @Override
    public void setOnClickListener(CaseField field) {
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseRegisterAdapter adapter = new CaseRegisterAdapter(activity, fields);

                RecyclerView.LayoutManager layout = new LinearLayoutManager(activity);
                layout.setAutoMeasureEnabled(true);
                fieldList.setLayoutManager(layout);
                fieldList.setAdapter(adapter);
                parent.removeAllViews();
                parent.addView(container);
            }
        });
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        container = (LinearLayout) inflater
                .inflate(R.layout.form_subform_layout, parent, false);

        fieldList = (RecyclerView) container.findViewById(R.id.field_list);
    }
}
