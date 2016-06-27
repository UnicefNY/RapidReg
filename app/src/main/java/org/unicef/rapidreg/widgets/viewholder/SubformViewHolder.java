package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseRegisterAdapter;
import org.unicef.rapidreg.forms.childcase.CaseField;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubformViewHolder extends BaseViewHolder<CaseField> {

    @BindView(R.id.show_subform)
    Button showSubform;

    private CaseActivity activity;
    private ViewGroup parent;
    private LinearLayout container;
    private List<CaseField> fields;
    private RecyclerView fieldList;
    private Button addSubForm;
    private Button deleteSubForm;

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
        showSubform.setText(String.format("%s %s", showSubform.getText(), field.getDisplayName().get("en")));
    }

    @Override
    public void setOnClickListener(CaseField field) {
        showSubform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseRegisterAdapter adapter = new CaseRegisterAdapter(activity, fields);

                RecyclerView.LayoutManager layout = new LinearLayoutManager(activity);
                layout.setAutoMeasureEnabled(true);
                fieldList.setLayoutManager(layout);
                fieldList.setAdapter(adapter);
                showSubform.setVisibility(View.GONE);
                parent.addView(container);
            }
        });

        addSubForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                ViewGroup container = (LinearLayout) inflater
                        .inflate(R.layout.form_subform_layout, parent, false);
                RecyclerView fieldList = (RecyclerView) container.findViewById(R.id.field_list);

                CaseRegisterAdapter adapter = new CaseRegisterAdapter(activity, fields);
                RecyclerView.LayoutManager layout = new LinearLayoutManager(activity);
                layout.setAutoMeasureEnabled(true);
                fieldList.setLayoutManager(layout);
                fieldList.setAdapter(adapter);
                parent.addView(container);
            }
        });

        deleteSubForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.removeView((View) v.getParent());

                if (parent.getChildCount() == 1) {
                    showSubform.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        container = (LinearLayout) inflater
                .inflate(R.layout.form_subform_layout, parent, false);

        fieldList = (RecyclerView) container.findViewById(R.id.field_list);
        addSubForm = (Button) container.findViewById(R.id.add);
        deleteSubForm = (Button) container.findViewById(R.id.delete);
    }
}
