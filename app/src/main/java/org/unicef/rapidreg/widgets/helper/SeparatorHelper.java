package org.unicef.rapidreg.widgets.helper;

import android.content.Context;
import android.view.View;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class SeparatorHelper extends BaseWidgetHelper implements WidgetHelper {

    public SeparatorHelper(Context context, CaseField field) {
        super(context, field);
    }

    @Override
    public View getConvertView() {
        View view = new View(context);
        view.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void setValue() {

    }

    @Override
    public void setOnClickListener() {

    }
}
