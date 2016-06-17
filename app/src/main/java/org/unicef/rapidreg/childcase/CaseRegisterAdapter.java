package org.unicef.rapidreg.childcase;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.widgets.helper.WidgetHelper;
import org.unicef.rapidreg.widgets.helper.WidgetHelperFactory;

import java.io.Serializable;
import java.util.List;

public class CaseRegisterAdapter extends ArrayAdapter<CaseField> {

    public CaseRegisterAdapter(Context context, int resource, List<CaseField> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CaseField field = getItem(position);
        WidgetHelper widgetHelper = WidgetHelperFactory.getWidgetHelper(getContext(), field);
        widgetHelper.setValue();
        Serializable caseMode = ((Activity) getContext()).getIntent().getSerializableExtra(CaseActivity.INTENT_KEY_CASE_MODE);

        if (CaseActivity.CaseMode.DETAIL != caseMode) {
            widgetHelper.setOnClickListener();
        }
        return widgetHelper.getConvertView();
    }
}
