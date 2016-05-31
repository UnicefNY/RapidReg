package org.unicef.rapidreg.cases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.forms.CaseFormSection;
import org.unicef.rapidreg.model.forms.CaseFormField;

import java.util.List;

public class CasesRegisterAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<CaseFormSection> formSections;

    public CasesRegisterAdapter(Context context, List<CaseFormSection> formSections) {
        this.context = context;
        this.formSections = formSections;
    }

    @Override
    public int getGroupCount() {
        return formSections.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<CaseFormField> formCaseFormFields = formSections.get(groupPosition).getCaseFormFields();
        return formCaseFormFields.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return formSections.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<CaseFormField> formCaseFormFields = formSections.get(groupPosition).getCaseFormFields();
        return formCaseFormFields.get(childPosition);
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
        CaseFormSection formSection = (CaseFormSection) getGroup(groupPosition);
        return createFormSectionView(formSection, convertView);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CaseFormField formCaseFormField = (CaseFormField) getChild(groupPosition, childPosition);
        return createFormFieldView(formCaseFormField, convertView);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private View createFormSectionView(CaseFormSection formSection, View convertView) {
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_heading, null);
        }
        TextView heading = (TextView) convertView.findViewById(R.id.heading);
        heading.setText(formSection.getName().get("en"));

        return convertView;
    }

    private View createFormFieldView(CaseFormField formCaseFormField, View convertView) {
        String fieldType = formCaseFormField.getType();
        if(fieldType.equals("select_box") && formCaseFormField.getMultiSelect()){ fieldType = "multi_select_box"; }
        int resourceId = getFieldLayoutId(fieldType);

        if (resourceId > 0) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(resourceId, null);
            return convertView;
        }
        return null;
    }

    protected int getFieldLayoutId(String fieldType) {
        return context.getResources().getIdentifier("form_" + fieldType, "layout", context.getPackageName());
    }
}