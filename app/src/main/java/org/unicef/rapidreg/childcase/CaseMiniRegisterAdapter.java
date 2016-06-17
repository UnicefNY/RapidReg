package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.widgets.helper.WidgetHelper;
import org.unicef.rapidreg.widgets.helper.WidgetHelperFactory;

import java.util.List;

public class CaseMiniRegisterAdapter extends ArrayAdapter<CaseField> {

    public CaseMiniRegisterAdapter(Context context, int resource, List<CaseField> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CaseField field = getItem(position);
        WidgetHelper widgetHelper = WidgetHelperFactory.getWidgetHelper(getContext(), field);
        widgetHelper.setValue();
        widgetHelper.setOnClickListener();

        return widgetHelper.getConvertView();
    }
}
