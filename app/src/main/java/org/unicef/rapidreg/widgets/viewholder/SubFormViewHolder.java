package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
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

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class SubFormViewHolder extends BaseViewHolder<Field> {
    private static final String TAG = SubFormViewHolder.class.getSimpleName();

    @BindView(R.id.add_subform)
    Button addSubFormBtn;

    private RecordActivity activity;
    private ViewGroup parent;
    private List<Field> fields;
    private String fieldParent;
    private String displayParent;
    private List<Boolean> subformDropDownStatus;
    private Context context;

    public SubFormViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        this.context = context;
        ButterKnife.bind(this, itemView);
        activity = (RecordActivity) context;
        parent = (ViewGroup) itemView;
        subformDropDownStatus = new ArrayList<>(parent.getChildCount());
    }

    @Override
    public void setValue(Field field) {
        if (field.getSubForm() == null) {
            return;
        }
        fields = removeSeparatorFields(field.getSubForm().getFields());
        fieldParent = field.getName();
        displayParent = field.getDisplayName().get(PrimeroAppConfiguration.getDefaultLanguage());

        attachParentToFields(fields, fieldParent);
        addSubFormBtn.setText(String.format("%s %s", context.getString(R.string.add), displayParent));
        addSubFormBtn.setVisibility(activity.getCurrentFeature().isEditMode() ?
                View.VISIBLE : GONE);
        restoreSubForms();
    }

    @Override
    public void setOnClickListener(Field field) {
        addSubFormBtn.setOnClickListener(v -> {
            Section subForm = field.getSubForm();
            if (subForm == null) {
                Toast.makeText(context, R.string.no_filed_exists_in_subform, Toast.LENGTH_SHORT).show();
                return;
            }
            itemValues.addChild(fieldParent, new HashMap<>());
            addSubForm(itemValues.getChildrenSize(fieldParent) - 1, true);
        });
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    @Override
    public void setFieldClickable(boolean clickable) {

    }

    private void initDeleteBtn(ViewGroup container) {
        final Button deleteBtn = (Button) container.findViewById(R.id.delete_subform);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageDialog messageDialog = new MessageDialog(context);
                messageDialog.setTitle(R.string.delete);
                messageDialog.setMessage(R.string.delete_subform);
                messageDialog.setPositiveButton(R.string.delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubFormViewHolder.this.deleteSubForm(view);
                        messageDialog.dismiss();
                    }
                });
                messageDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageDialog.dismiss();
                    }
                });
                messageDialog.show();
            }
        });
        deleteBtn.setVisibility(activity.getCurrentFeature().isEditMode() ?
                View.VISIBLE : GONE);
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
                itemValues.getChildAsItemValues(fieldParent, index), fieldValueVerifyResult, false);
        fieldList.setAdapter(adapter);

        boolean visibleStatus = subformDropDownStatus.get(index);
        if (visibleStatus) {
            enableContainer(container);
        } else {
            disableContainer(container);
        }
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

    private void addSubForm(int index, boolean isDropDown) {
        subformDropDownStatus.add(index, isDropDown);
        LayoutInflater inflater = LayoutInflater.from(activity);
        ViewGroup container = (LinearLayout) inflater
                .inflate(R.layout.form_subform, parent, false);
        initDeleteBtn(container);
        initFieldList(container, index);

        parent.addView(container, index);
        setSubFormTitleClickEvent(container, index);
    }

    private void setSubFormTitleClickEvent(final ViewGroup container, final int index) {
        LinearLayout subFormTitleLayout = (LinearLayout) container.findViewById(R.id.sub_form_title_layout);
        TextView subFormTitle = (TextView) container.findViewById(R.id.sub_form_title);
        subFormTitle.setText(displayParent);
        final View fieldList = container.findViewById(R.id.field_list);

        subFormTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GONE == fieldList.getVisibility()) {
                    enableContainer(container);
                    subformDropDownStatus.remove(index);
                    subformDropDownStatus.add(index, true);
                } else {
                    disableContainer(container);
                    subformDropDownStatus.remove(index);
                    subformDropDownStatus.add(index, false);
                }
            }
        });
    }

    private void enableContainer(View container) {
        final View fieldList = container.findViewById(R.id.field_list);
        final Button deleteBtn = (Button) container.findViewById(R.id.delete_subform);
        final ImageView arrow = (ImageView) container.findViewById(R.id.sub_form_title_arrow);

        fieldList.setVisibility(View.VISIBLE);
        if (activity.getCurrentFeature().isEditMode()) {
            deleteBtn.setVisibility(View.VISIBLE);
        }
        arrow.setImageResource(R.drawable.arrow_down_blue);
    }

    private void disableContainer(View container) {
        final View fieldList = container.findViewById(R.id.field_list);
        final Button deleteBtn = (Button) container.findViewById(R.id.delete_subform);
        final ImageView arrow = (ImageView) container.findViewById(R.id.sub_form_title_arrow);

        fieldList.setVisibility(GONE);
        deleteBtn.setVisibility(GONE);
        arrow.setImageResource(R.drawable.arrow_up_blue);
    }

    private void restoreSubForms() {
        List<Map<String, Object>> childrenArray = itemValues.getChildrenAsJsonArray(fieldParent);
        if (childrenArray == null) {
            return;
        }
        for (int i = 0; i < childrenArray.size(); i++) {
            addSubForm(i, false);
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

    public void setSubformVisible(List<Boolean> visibleStatus) {
        if (visibleStatus.isEmpty() || subformDropDownStatus.isEmpty()) {
            return;
        }

        int subformSize = visibleStatus.size() > subformDropDownStatus.size() ?
                subformDropDownStatus.size() : visibleStatus.size();

        for (int index = 0; index < subformSize; index++) {
            boolean status = visibleStatus.get(index);
            subformDropDownStatus.remove(index);
            subformDropDownStatus.add(index, status);

            if (status) {
                enableContainer(parent.getChildAt(index));
            } else {
                disableContainer(parent.getChildAt(index));
            }
        }
    }

    public List<Boolean> getSubformStatusList() {
        return subformDropDownStatus;
    }
}
