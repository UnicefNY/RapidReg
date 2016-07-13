package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseRegisterAdapter;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubFormViewHolder extends BaseViewHolder<Field> {
    public static final int NUM_CHILD_VIEWS = 2;

    @BindView(R.id.add_subform)
    Button addSubFormBtn;

    private CaseActivity activity;
    private ViewGroup parent;
    private List<Field> fields;
    private String fieldParent;

    public SubFormViewHolder(Context context, View itemView, ItemValues itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
        activity = (CaseActivity) context;
        parent = (ViewGroup) itemView;
    }

    @Override
    public void setValue(Field field) {
        fields = field.getSubForm().getFields();
        fieldParent = field.getDisplayName().get(Locale.getDefault().getLanguage());

        attachParentToFields(fields, fieldParent);
        addSubFormBtn.setText(String.format("%s %s", context.getString(R.string.add), fieldParent));
        addSubFormBtn.setVisibility(activity.getCurrentFeature().isEditMode() ?
                View.VISIBLE : View.GONE);
        restoreSubForms();
    }

    @Override
    public void setOnClickListener(Field field) {
        addSubFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemValues.addChild(fieldParent, new JsonObject());
                addSubForm(itemValues.getChildrenSize(fieldParent) - 1);
            }
        });
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    private void clearFocus(View v) {
        View container = (View) v.getParent();
        View fieldList = container.findViewById(R.id.field_list);
        fieldList.clearFocus();
    }

    private void initDeleteBtn(ViewGroup container) {
        Button deleteBtn = (Button) container.findViewById(R.id.delete_subform);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocus(v);
                removeSubForm(parent.indexOfChild((View) v.getParent()));
                parent.removeView((View) v.getParent());
                updateIndexForFields();
            }
        });
        deleteBtn.setVisibility(activity.getCurrentFeature().isEditMode() ?
                View.VISIBLE : View.GONE);
    }

    private void initFieldList(ViewGroup container, int index) {
        RecyclerView fieldList = (RecyclerView) container.findViewById(R.id.field_list);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(activity);
        layout.setAutoMeasureEnabled(true);
        fieldList.setLayoutManager(layout);

        List<Field> fields = cloneFields();
        assignIndexForFields(fields, index);

        CaseRegisterAdapter adapter = new CaseRegisterAdapter(activity, fields,
                itemValues.getChildAsItemValues(fieldParent, index), false);

        fieldList.setAdapter(adapter);
    }

    private void attachParentToFields(List<Field> fields, String parent) {
        for (Field field : fields) {
            field.setParent(parent);
        }
    }

    private void assignIndexForFields(List<Field> fields, int index) {
        for (Field field : fields) {
            field.setIndex(index);
        }
    }

    private void updateIndexForFields() {
        for (int i = 0; i < itemValues.getChildrenAsJsonArray(fieldParent).size(); i++) {
            View child = parent.getChildAt(i);
            RecyclerView fieldList = (RecyclerView) child.findViewById(R.id.field_list);
            CaseRegisterAdapter adapter = (CaseRegisterAdapter) fieldList.getAdapter();
            List<Field> fields = adapter.getFields();
            assignIndexForFields(fields, i);
        }
    }

    private List<Field> cloneFields() {
        List<Field> fieldList = new ArrayList<>();

        for (Field field : fields) {
            fieldList.add(field.copy());
        }

        return fieldList;
    }

    private void removeSubForm(int index) {
        JsonArray values = itemValues.getChildrenAsJsonArray(fieldParent);
        if (values != null) {
            values.remove(index);
        }
    }

    private void addSubForm(int index) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        ViewGroup container = (LinearLayout) inflater
                .inflate(R.layout.form_subform, parent, false);

        initDeleteBtn(container);
        initFieldList(container, index);
        parent.addView(container, index);
    }

    private void restoreSubForms() {
        JsonArray childrenArray = itemValues.getChildrenAsJsonArray(fieldParent);
        if (childrenArray == null) {
            return;
        }
        for (int i = 0; i < childrenArray.size(); i++) {
            addSubForm(i);
        }
    }
}
