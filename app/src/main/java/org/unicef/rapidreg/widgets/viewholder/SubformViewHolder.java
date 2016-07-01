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
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubformViewHolder extends BaseViewHolder<CaseField> {
    public static final int NUM_CHILD_VIEWS = 2;

    @BindView(R.id.add_subform)
    Button addSubformBtn;

    private CaseActivity activity;
    private ViewGroup parent;
    private List<CaseField> fields;
    private String fieldParent;

    public SubformViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        activity = (CaseActivity) context;
        parent = (ViewGroup) itemView;
    }

    @Override
    public void setValue(CaseField field) {
        fields = field.getSubForm().getFields();
        fieldParent = field.getDisplayName().get("en");

        attachParentToFields(fields, fieldParent);
        addSubformBtn.setText(String.format("%s %s", addSubformBtn.getText(), fieldParent));

        restoreSubforms();
    }

    @Override
    public void setOnClickListener(CaseField field) {
        addSubformBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubform();
            }
        });
    }

    @Override
    protected String getResult() {
        return null;
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    private int getInsertIndex() {
        return parent.getChildCount() - NUM_CHILD_VIEWS;
    }

    private void initDeleteBtn(ViewGroup container) {
        Button deleteBtn = (Button) container.findViewById(R.id.delete_subform);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSubformCache(parent.indexOfChild((View) v.getParent()));
                parent.removeView((View) v.getParent());
                updateIndexForFields();
            }
        });
        deleteBtn.setVisibility(activity.getCurrentFeature().isInEditMode() ?
                View.VISIBLE : View.GONE);
    }

    private void initFieldList(ViewGroup container) {
        RecyclerView fieldList = (RecyclerView) container.findViewById(R.id.field_list);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(activity);
        layout.setAutoMeasureEnabled(true);
        fieldList.setLayoutManager(layout);

        List<CaseField> fields = cloneFields();
        assignIndexForFields(fields, getInsertIndex());
        CaseRegisterAdapter adapter = new CaseRegisterAdapter(activity, fields, false);
        fieldList.setAdapter(adapter);
    }

    private void attachParentToFields(List<CaseField> fields, String parent) {
        for (CaseField field : fields) {
            field.setParent(parent);
        }
    }

    private void assignIndexForFields(List<CaseField> fields, int index) {
        for (CaseField field : fields) {
            field.setIndex(index);
        }
    }

    private void updateIndexForFields() {
        for (int i = 0; i < getInsertIndex(); i++) {
            View child = parent.getChildAt(i);
            RecyclerView fieldList = (RecyclerView) child.findViewById(R.id.field_list);
            CaseRegisterAdapter adapter = (CaseRegisterAdapter) fieldList.getAdapter();
            List<CaseField> fields = adapter.getFields();
            assignIndexForFields(fields, i);
        }
    }

    private List<CaseField> cloneFields() {
        List<CaseField> fieldList = new ArrayList<>();

        for (CaseField field : fields) {
            fieldList.add(field.copy());
        }

        return fieldList;
    }

    private void updateSubformCache(int index) {
        List<Map<String, String>> values = SubformCache.get(fieldParent);
        if (values != null) {
            values.remove(index);
        }
    }

    private void addSubform() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        ViewGroup container = (LinearLayout) inflater
                .inflate(R.layout.form_subform, parent, false);

        initDeleteBtn(container);
        initFieldList(container);
        parent.addView(container, getInsertIndex());
    }

    private void restoreSubforms() {
        List<Map<String, String>> values = SubformCache.get(fieldParent) == null ?
                new ArrayList<Map<String, String>>() : SubformCache.get(fieldParent);

        for (int i = 0; i < values.size(); i++) {
            addSubform();
        }
    }
}
