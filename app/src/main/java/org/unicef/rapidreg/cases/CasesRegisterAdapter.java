package org.unicef.rapidreg.cases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.forms.cases.CaseFieldBean;
import org.unicef.rapidreg.model.forms.cases.CaseFormBean;
import org.unicef.rapidreg.model.forms.cases.CaseSectionBean;

import java.util.List;

public class CasesRegisterAdapter extends BaseExpandableListAdapter {
    public static final String TAG = CasesRegisterAdapter.class.getSimpleName();
    private Context context;
    private List<CaseSectionBean> sections;

    public CasesRegisterAdapter(Context context, List<CaseSectionBean> sections) {
        this.context = context;
        this.sections = sections;
    }

    @Override
    public int getGroupCount() {
        return sections.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<CaseFieldBean> fields = sections.get(groupPosition).getFields();
        return fields.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return sections.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<CaseFieldBean> fields = sections.get(groupPosition).getFields();
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
        CaseSectionBean section = (CaseSectionBean) getGroup(groupPosition);
        return createFormSectionView(section, convertView);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CaseFieldBean field = (CaseFieldBean) getChild(groupPosition, childPosition);
        convertView = createFormFieldView(field, convertView);
        TextView tvFormLable = (TextView) convertView.findViewById(R.id.label);
        tvFormLable.setText(field.getDisplayName().get("en"));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private View createFormSectionView(CaseSectionBean section, View convertView) {
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_heading, null);
        }
        TextView heading = (TextView) convertView.findViewById(R.id.heading);
        heading.setText(section.getName().get("en"));
        return convertView;
    }

    private View createFormFieldView(CaseFieldBean caseFormField, View convertView) {
        String fieldType = caseFormField.getType();
        LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int resourceId = getFieldLayoutId(fieldType);
        if (resourceId > 0) {
<<<<<<< HEAD
            convertView = infalInflater.inflate(resourceId, null);
=======
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null);
>>>>>>> refactor: change method's name
            return convertView;
        }
        return null;
    }

    protected int getFieldLayoutId(String fieldType) {
<<<<<<< HEAD
//        return context.getResources().getIdentifier("form_" + fieldType, "layout", context.getPackageName());
=======
>>>>>>> refactor: change method's name
        if (fieldType.equals("tick_box")) {
            return context.getResources().getIdentifier("form_tick_box", "layout", context.getPackageName());
        }
        return context.getResources().getIdentifier("form_text_field", "layout", context.getPackageName());
    }

}
