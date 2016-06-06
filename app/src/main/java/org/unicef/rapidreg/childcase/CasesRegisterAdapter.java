package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.form.childcase.CaseField;
import org.unicef.rapidreg.form.childcase.CaseSection;

import java.util.List;

public class CasesRegisterAdapter extends BaseExpandableListAdapter {
    public static final String TAG = CasesRegisterAdapter.class.getSimpleName();
    private Context context;
    private List<CaseSection> sections;

    public CasesRegisterAdapter(Context context, List<CaseSection> sections) {
        this.context = context;
        this.sections = sections;
    }

    @Override
    public int getGroupCount() {
        return sections.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<CaseField> fields = sections.get(groupPosition).getFields();
        return fields.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return sections.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<CaseField> fields = sections.get(groupPosition).getFields();
        return fields.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        CaseSection section = (CaseSection) getGroup(groupPosition);
        return createFormSectionView(section, convertView);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CaseField field = (CaseField) getChild(groupPosition, childPosition);
        convertView = createFormFieldView(field, convertView);
        TextView tvFormLabel = (TextView) convertView.findViewById(R.id.label);
        tvFormLabel.setText(field.getDisplayName().get("en"));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private View createFormSectionView(CaseSection section, View convertView) {
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_heading, null);
        }
        TextView heading = (TextView) convertView.findViewById(R.id.heading);
        heading.setText(section.getName().get("en"));
        return convertView;
    }

    private View createFormFieldView(CaseField caseFormField, View convertView) {
        String fieldType = caseFormField.getType();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int resourceId = getFieldLayoutId(fieldType);
        if (resourceId > 0) {
            convertView = inflater.inflate(resourceId, null);
            return convertView;
        }
        return null;
    }

    protected int getFieldLayoutId(String fieldType) {
//        return context.getResources().getIdentifier("form_" + fieldType, "layout", context.getPackageName());
        if (fieldType.equals("tick_box")) {
            return context.getResources().getIdentifier("form_tick_box", "layout", context.getPackageName());
        }
        return context.getResources().getIdentifier("form_text_field", "layout", context.getPackageName());
    }

}
