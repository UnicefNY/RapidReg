package org.unicef.rapidreg.widgets.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordRegisterAdapter;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubFormViewHolder extends BaseViewHolder<Field> {
    @BindView(R.id.add_subform)
    Button addSubFormBtn;

    private RecordActivity activity;
    private ViewGroup parent;
    private List<Field> fields;
    private String fieldParent;
    private String displayParent;

    public SubFormViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
        activity = (RecordActivity) context;
        parent = (ViewGroup) itemView;
    }

    @Override
    public void setValue(Field field) {
        fieldParent = field.getName();
        displayParent = field.getDisplayName().get(Locale.getDefault().getLanguage());
        addSubFormBtn.setText(String.format("%s %s", context.getString(R.string.add), displayParent));
        addSubFormBtn.setVisibility(activity.getCurrentFeature().isEditMode() ?
                View.VISIBLE : View.GONE);
        if (field.getSubForm() == null) {
            return;
        }
        fields = removeSeparatorFields(field.getSubForm().getFields());
        attachParentToFields(fields, fieldParent);
        restoreSubForms();
    }

    @Override
    public void setOnClickListener(final Field field) {
        addSubFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (field.getSubForm() == null) {
                    Toast.makeText(context, R.string.no_filed_exists_in_subform, Toast.LENGTH_SHORT).show();
                    return;
                }
                itemValues.addChild(fieldParent, new HashMap<String, Object>());
                addSubForm(itemValues.getChildrenSize(fieldParent) - 1);
            }
        });
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    private void initDeleteBtn(ViewGroup container) {
        final Button deleteBtn = (Button) container.findViewById(R.id.delete_subform);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_subform)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteSubForm(v);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
        deleteBtn.setVisibility(activity.getCurrentFeature().isEditMode() ?
                View.VISIBLE : View.GONE);
    }

    private void deleteSubForm(View v) {
        removeSubForm(parent.indexOfChild((View) v.getParent()));
        parent.removeView((View) v.getParent());
        updateIndexForFields();
    }

    private void initFieldList(ViewGroup container, int index) {
        RecyclerView fieldList = (RecyclerView) container.findViewById(R.id.field_list);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(activity);
        layout.setAutoMeasureEnabled(true);
        fieldList.setLayoutManager(layout);

        List<Field> fields = cloneFields();
        assignIndexForFields(fields, index);

        RecordRegisterAdapter adapter = new RecordRegisterAdapter(activity, fields,
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
            RecordRegisterAdapter adapter = (RecordRegisterAdapter) fieldList.getAdapter();
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
        List<Map<String, Object>> values = itemValues.getChildrenAsJsonArray(fieldParent);
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
        setSubFormTitleClickEvent(container);
    }

    private void setSubFormTitleClickEvent(ViewGroup container) {
        LinearLayout subFormTitleLayout = (LinearLayout) container.findViewById(R.id.sub_form_title_layout);
        TextView subFormTitle = (TextView) container.findViewById(R.id.sub_form_title);
        subFormTitle.setText(displayParent);
        final View fieldList = container.findViewById(R.id.field_list);
        final Button deleteBtn = (Button) container.findViewById(R.id.delete_subform);
        final ImageView arrow = (ImageView) container.findViewById(R.id.sub_form_title_arrow);

        subFormTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (View.GONE == fieldList.getVisibility()) {
                    fieldList.setVisibility(View.VISIBLE);
                    if (activity.getCurrentFeature().isEditMode()) {
                        deleteBtn.setVisibility(View.VISIBLE);
                    }
                    arrow.setImageResource(R.drawable.arrow_down_blue);
                } else {
                    fieldList.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.arrow_up_blue);
                }
            }
        });
    }

    private void restoreSubForms() {
        List<Map<String, Object>> childrenArray = itemValues.getChildrenAsJsonArray(fieldParent);
        if (childrenArray == null) {
            return;
        }
        for (int i = 0; i < childrenArray.size(); i++) {
            addSubForm(i);
        }
    }

    private List<Field> removeSeparatorFields(List<Field> fields) {
        Iterator<Field> iterator = fields.iterator();

        while (iterator.hasNext()) {
            Field field = iterator.next();
            if (field.isSeparator()) {
                iterator.remove();
            }
        }

        return fields;
    }
}
