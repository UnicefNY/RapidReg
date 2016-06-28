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
    public static final int NUM_CHILD_VIEWS = 2;

    @BindView(R.id.add_subform)
    Button addSubform;

    private CaseActivity activity;
    private ViewGroup parent;
    private List<CaseField> fields;

    public SubformViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        activity = (CaseActivity) context;
        parent = (ViewGroup) itemView;
    }

    @Override
    public void setValue(CaseField field) {
        fields = field.getSubForm().getFields();
        addSubform.setText(String.format("%s %s", addSubform.getText(),
                field.getDisplayName().get("en")));
    }

    @Override
    public void setOnClickListener(CaseField field) {
        addSubform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                ViewGroup container = (LinearLayout) inflater
                        .inflate(R.layout.form_subform, parent, false);

                initDeleteBtn(container);
                initFieldList(container);
                parent.addView(container, getInsertIndex());
            }
        });
    }

    private int getInsertIndex() {
        return parent.getChildCount() - NUM_CHILD_VIEWS;
    }

    private void initDeleteBtn(ViewGroup container) {
        Button deleteBtn = (Button) container.findViewById(R.id.delete_subform);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.removeView((View) v.getParent());
            }
        });
    }

    private void initFieldList(ViewGroup container) {
        RecyclerView fieldList = (RecyclerView) container.findViewById(R.id.field_list);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(activity);
        layout.setAutoMeasureEnabled(true);
        fieldList.setLayoutManager(layout);
        CaseRegisterAdapter adapter = new CaseRegisterAdapter(activity, fields);
        fieldList.setAdapter(adapter);
    }
}
